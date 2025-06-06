package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.RemitApplicationRecord;
import com.starchain.common.entity.RemitCallbackRecord;
import com.starchain.common.entity.UserWalletBalance;
import com.starchain.common.entity.UserWalletTransaction;
import com.starchain.common.entity.response.MiPayRemitNotifyResponse;
import com.starchain.common.enums.*;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.dao.RemitCallbackRecordMapper;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.IRemitCallbackRecordService;
import com.starchain.service.IUserWalletBalanceService;
import com.starchain.service.IUserWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2025-01-06
 * @Description 申请汇款通知 处理类
 */
@Service
@Slf4j
public class RemitCallbackRecordServiceImpl extends ServiceImpl<RemitCallbackRecordMapper, RemitCallbackRecord> implements IRemitCallbackRecordService {
    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;

    @Autowired
    private IRemitApplicationRecordService remitApplicationRecordService;

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
            // 3. 检查是否已经处理成功（幂等性）
            if (remitApplicationRecord.getStatus() == CreateStatusEnum.SUCCESS.getCode()) {
                log.info("充值记录已处理成功，无需重复处理，通知ID: {}", miPayRemitNotifyResponse.getNotifyId());
                return true;
            }
            // 4. 查询或创建回调记录
            RemitCallbackRecord callbackRecord = createOrUpdateCallbackRecord(miPayRemitNotifyResponse);

            // 6.对用户钱包余额进行扣款
            return handleRechargeStatus(userWalletBalance, miPayRemitNotifyResponse, remitApplicationRecord, callbackRecord);
        } catch (Exception e) {
            log.error("申请汇款, 通知ID: {}, 错误信息: {}", miPayRemitNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("申请汇款处理失败");
        }
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
        Assert.isTrue(record.getStatus().equals(CreateStatusEnum.CREATING.getCode()), "数据异常，申请汇款已处理完成");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 5. 查询或创建回调记录
    private RemitCallbackRecord createOrUpdateCallbackRecord(MiPayRemitNotifyResponse response) {
        LambdaQueryWrapper<RemitCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(RemitCallbackRecord::getRemitCode, response.getRemitCode())
                .eq(RemitCallbackRecord::getBusinessType, response.getBusinessType());

        RemitCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new RemitCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setRemitCode(response.getRemitCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setTradeId(response.getTradeId());

            callbackRecord.setStatus(response.getStatus());
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

    // 处理汇款成功或者失败场景
    private boolean handleRechargeStatus(UserWalletBalance userWalletBalance, MiPayRemitNotifyResponse response, RemitApplicationRecord remitApplicationRecord, RemitCallbackRecord callbackRecord) {
        if (CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {
            // 扣除冻结金额
            userWalletBalanceService.deductionFreezeBalance(remitApplicationRecord.getUserId(), remitApplicationRecord.getBusinessId(), remitApplicationRecord.getFromAmount(), remitApplicationRecord.getHandlingFeeAmount());
            // 修改卡汇款申请记录状态为成功
            updateRecordStatus(response);
            // 更新卡汇款回调记录
            updateCallbackRecord(callbackRecord, response);
            return true;
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            List<UserWalletTransaction> transactions = new ArrayList<>();
            // 回退金额生产流水
            // 回退冻结金额 + 手续费
            BigDecimal avaBalance = userWalletBalance.getAvaBalance();
            // 充值金额交易流水落库
            UserWalletTransaction rechargeTransaction = createUserWalletTransaction(remitApplicationRecord.getUserId(), avaBalance, remitApplicationRecord.getFromAmount(), TransactionTypeEnum.GLOBAL_REMITTANCE_FEE, remitApplicationRecord.getBankCode(), remitApplicationRecord.getOrderId(), remitApplicationRecord.getTradeId());
            transactions.add(rechargeTransaction);
            avaBalance = avaBalance.add(remitApplicationRecord.getFromAmount());

            // 手续费交易流水落库
            UserWalletTransaction feeTransaction = createUserWalletTransaction(remitApplicationRecord.getUserId(), avaBalance, remitApplicationRecord.getHandlingFeeAmount(), TransactionTypeEnum.REMIT_FEE, remitApplicationRecord.getBankCode(), remitApplicationRecord.getOrderId(), remitApplicationRecord.getTradeId());
            transactions.add(feeTransaction);

            // 保存所有交易记录
            userWalletTransactionService.saveBatch(transactions);

            // 冻结金额 返回可用余额
            userWalletBalanceService.rollbackFreezeBalance(remitApplicationRecord.getUserId(), remitApplicationRecord.getBusinessId(), remitApplicationRecord.getFromAmount().add(remitApplicationRecord.getHandlingFeeAmount()));
            handleFailedStatus(callbackRecord, response);
            return true;
        }
        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    private UserWalletTransaction createUserWalletTransaction(Long userId, BigDecimal avaBalance, BigDecimal fromAmount, TransactionTypeEnum transactionTypeEnum, String businessNumber, String orderId, String tradeId) {
        return UserWalletTransaction.builder()
                .userId(userId)
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .balance(avaBalance)
                .amount(fromAmount)
                .finaBalance(avaBalance.subtract(fromAmount))
                .type(transactionTypeEnum.getCode())
                .businessNumber(businessNumber)
                .createTime(LocalDateTime.now())
                .partitionKey(DateUtil.getMonth())
                .remark(transactionTypeEnum.getDescription())
                .tradeId(tradeId)
                .orderId(orderId)
                .build();
    }

    // 修改卡充值状态为成功
    private void updateRecordStatus(MiPayRemitNotifyResponse response) {
        LambdaUpdateWrapper<RemitApplicationRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitApplicationRecord::getTradeId, response.getTradeId())
                .eq(RemitApplicationRecord::getRemitCode, response.getRemitCode())
                .set(RemitApplicationRecord::getStatus, CreateStatusEnum.SUCCESS.getCode())
                .set(RemitApplicationRecord::getUpdateTime, LocalDateTime.now())
                .set(RemitApplicationRecord::getFinishTime, LocalDateTime.now());
        boolean isUpdated = remitApplicationRecordService.update(updateWrapper);
        if (!isUpdated) {
            log.warn("充值记录状态更新失败，可能已被其他线程处理，通知ID: {}", response.getNotifyId());
        }
    }


    // 更新回调记录
    private void updateCallbackRecord(RemitCallbackRecord callbackRecord, MiPayRemitNotifyResponse response) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setStatus(response.getStatus());
        callbackRecord.setStatusDesc(response.getStatusDesc());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    // 处理失败状态
    private void handleFailedStatus(RemitCallbackRecord callbackRecord, MiPayRemitNotifyResponse response) {
        LambdaUpdateWrapper<RemitCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .set(RemitCallbackRecord::getStatus, CardStatusDescEnum.FAILED.getDescription())
                .set(RemitCallbackRecord::getStatusDesc, response.getStatusDesc())
                .set(RemitCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }

    // 更新用户钱包余额
    private void updateUserWalletBalance(RemitApplicationRecord rechargeRecord) {
        LambdaUpdateWrapper<UserWalletBalance> balanceUpdateWrapper = new LambdaUpdateWrapper<>();
        balanceUpdateWrapper.eq(UserWalletBalance::getUserId, rechargeRecord.getUserId())
                .setSql("balance = balance - " + rechargeRecord.getFromAmount()) // 银行卡端实际扣款金额为准
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
        userWalletBalanceService.update(balanceUpdateWrapper);
    }
}
