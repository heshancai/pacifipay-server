package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.RemitApplicationRecord;
import com.starchain.common.entity.RemitCancelCallbackRecord;
import com.starchain.common.entity.UserWalletBalance;
import com.starchain.common.entity.UserWalletTransaction;
import com.starchain.common.entity.response.MiPayRemitNotifyResponse;
import com.starchain.common.enums.CardStatusDescEnum;
import com.starchain.common.enums.CreateStatusEnum;
import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.common.enums.TransactionTypeEnum;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.dao.RemitCancelCallbackRecordMapper;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.IRemitCancelCallbackRecordService;
import com.starchain.service.IUserWalletBalanceService;
import com.starchain.service.IUserWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description 汇款撤销通知
 */
@Service
@Slf4j
public class RemitCancelCallbackRecordServiceImpl extends ServiceImpl<RemitCancelCallbackRecordMapper, RemitCancelCallbackRecord> implements IRemitCancelCallbackRecordService {
    @Autowired
    private IRemitApplicationRecordService remitApplicationRecordService;
    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;
    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean callBack(String callBackJson) {
        MiPayRemitNotifyResponse miPayRemitNotifyResponse = this.covertToMiPayRemitNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayRemitNotifyResponse);

            // 2. 申请汇款记录是否存在 没有则抛出异常
            RemitApplicationRecord remitApplicationRecord = validateAndGetRecord(miPayRemitNotifyResponse);
            UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(remitApplicationRecord.getUserId(), remitApplicationRecord.getBusinessId());
            Assert.notNull(userWalletBalance, "用户钱包不存在");

            // 3. 数据已撤销（幂等性）
            if (remitApplicationRecord.getStatus() == CreateStatusEnum.CANCEL.getCode()) {
                log.info("充值已经撤销，无需重复处理，通知ID: {}", miPayRemitNotifyResponse.getNotifyId());
                return true;
            }
            // 4. 查询或创建回调记录
            RemitCancelCallbackRecord callbackRecord = createOrUpdateCallbackRecord(miPayRemitNotifyResponse);

            // 5.汇款撤销 返回金额 手续费不退回
            return handleRechargeStatus(miPayRemitNotifyResponse, remitApplicationRecord, callbackRecord);
        } catch (Exception e) {
            log.error("申请汇款, 通知ID: {}, 错误信息: {}", miPayRemitNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("申请汇款处理失败");
        }
    }

    /**
     * 记录汇款撤销交易流水
     * @param userWalletBalance
     * @param remitApplicationRecord
     * @param callbackRecord
     */
    private void createUserWalletTransaction(UserWalletBalance userWalletBalance, RemitApplicationRecord remitApplicationRecord, RemitCancelCallbackRecord callbackRecord) {
        // 手续费
        BigDecimal cancelAmount = callbackRecord.getCancelAmount();

        // 余额扣除手续费
        BigDecimal finalBalance = userWalletBalance.getAvaBalance().add(cancelAmount);

        UserWalletTransaction userWalletTransaction = UserWalletTransaction.builder()
                .userId(userWalletBalance.getUserId())
                .coinName(remitApplicationRecord.getFromMoneyKind())
                .balance(userWalletBalance.getAvaBalance())
                .amount(cancelAmount)
                .finaBalance(finalBalance)
                .type(TransactionTypeEnum.REMIT_CANCEL_FEE.getCode())
                .businessNumber(remitApplicationRecord.getRemitBankNo())
                .partitionKey(DateUtil.getMonth())
                .remark(TransactionTypeEnum.REMIT_CANCEL_FEE.getDescription())
                .createTime(LocalDateTime.now())
                .orderId(remitApplicationRecord.getOrderId())
                .tradeId(remitApplicationRecord.getTradeId()).build();
        userWalletTransactionService.save(userWalletTransaction);
        log.info("记录汇款撤销交易流水,交易信息为:{}", userWalletTransaction);
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayRemitNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.Remit.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实申请汇款的记录是否存在
    private RemitApplicationRecord validateAndGetRecord(MiPayRemitNotifyResponse response) {
        LambdaQueryWrapper<RemitApplicationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitApplicationRecord::getTradeId, response.getTradeId()).eq(RemitApplicationRecord::getOrderId, response.getOrderId())
                .eq(RemitApplicationRecord::getRemitCode, response.getRemitCode());
        RemitApplicationRecord record = remitApplicationRecordService.getOne(queryWrapper);
        Assert.isTrue(record != null, "数据异常，申请汇款记录不存在");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 5. 查询或创建回调记录
    private RemitCancelCallbackRecord createOrUpdateCallbackRecord(MiPayRemitNotifyResponse response) {
        LambdaQueryWrapper<RemitCancelCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCancelCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(RemitCancelCallbackRecord::getRemitCode, response.getRemitCode())
                .eq(RemitCancelCallbackRecord::getTradeId, response.getTradeId())
                .eq(RemitCancelCallbackRecord::getBusinessType, response.getBusinessType());

        RemitCancelCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new RemitCancelCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setRemitCode(response.getRemitCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setTradeId(response.getTradeId());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 处理重复通知 ，通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }


    // 汇款撤销
    private boolean handleRechargeStatus(MiPayRemitNotifyResponse response, RemitApplicationRecord remitApplicationRecord, RemitCancelCallbackRecord callbackRecord) {
        if (CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {

            LambdaQueryWrapper<UserWalletBalance> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserWalletBalance::getUserId, remitApplicationRecord.getUserId())
                    .eq(UserWalletBalance::getBusinessId, remitApplicationRecord.getBusinessId());
            UserWalletBalance userWalletBalance = userWalletBalanceService.getOne(queryWrapper);
            // 汇款撤销 退回汇款金额 不退手续费
            createUserWalletTransaction(userWalletBalance, remitApplicationRecord, callbackRecord);
            // 修改用户钱包余额
            updateUserWalletBalance(remitApplicationRecord, response.getCancelAmount());
            // 修改卡汇款申请记录状态为撤销
            updateRecordStatus(response);
            // 更新卡汇款回调记录为撤销
            updateCallbackRecord(callbackRecord, response);
            return true;
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            // 处理失败状态
            handleFailedStatus(callbackRecord);
            return true;
        }
        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    // 更新回调记录 为撤销
    private void updateCallbackRecord(RemitCancelCallbackRecord callbackRecord, MiPayRemitNotifyResponse response) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setStatusDesc(response.getStatusDesc());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    // 处理失败状态
    private void handleFailedStatus(RemitCancelCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<RemitCancelCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitCancelCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .set(RemitCancelCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }

    // 修改卡充值状态为撤销
    private void updateRecordStatus(MiPayRemitNotifyResponse response) {
        LambdaUpdateWrapper<RemitApplicationRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitApplicationRecord::getTradeId, response.getTradeId())
                .eq(RemitApplicationRecord::getRemitCode, response.getRemitCode())
                .eq(RemitApplicationRecord::getOrderId, response.getOrderId())
                .set(RemitApplicationRecord::getStatus, CreateStatusEnum.CANCEL.getCode())
                .set(RemitApplicationRecord::getUpdateTime, LocalDateTime.now())
                .set(RemitApplicationRecord::getFinishTime, LocalDateTime.now());
        boolean isUpdated = remitApplicationRecordService.update(updateWrapper);
        if (!isUpdated) {
            log.warn("充值记录状态更新失败，可能已被其他线程处理，通知ID: {}", response.getNotifyId());
        }
    }

    /**
     * 即使汇款撤销  也需要扣除手续费扣除手续费
     *
     * @param rechargeRecord
     * @param cancelAmount
     */
    private void updateUserWalletBalance(RemitApplicationRecord rechargeRecord, BigDecimal cancelAmount) {
        LambdaUpdateWrapper<UserWalletBalance> balanceUpdateWrapper = new LambdaUpdateWrapper<>();
        balanceUpdateWrapper.eq(UserWalletBalance::getUserId, rechargeRecord.getUserId()).eq(UserWalletBalance::getBusinessId, rechargeRecord.getBusinessId())
                .setSql("ava_balance = ava_balance +" + cancelAmount)
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
        userWalletBalanceService.update(balanceUpdateWrapper);
    }


}
