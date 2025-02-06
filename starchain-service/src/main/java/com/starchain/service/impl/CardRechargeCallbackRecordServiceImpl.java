package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.*;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.common.enums.*;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.dao.CardRechargeCallbackRecordMapper;
import com.starchain.service.*;
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
 * @Description 卡充值成功回调
 */
@Service
@Slf4j
public class CardRechargeCallbackRecordServiceImpl extends ServiceImpl<CardRechargeCallbackRecordMapper, CardRechargeCallbackRecord> implements ICardRechargeCallbackRecordService {

    @Autowired
    private ICardRechargeRecordService cardRechargeRecordService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;

    @Autowired
    private ICardService cardService;

    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean callBack(String callBackJson) {

        MiPayCardNotifyResponse miPayCardNotifyResponse = this.covertToMiPayCardNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 核实卡充值记录是否存在
            CardRechargeRecord rechargeRecord = validateAndGetRechargeRecord(miPayCardNotifyResponse);

            // 3. 检查是否已经处理成功（幂等性）
            if (rechargeRecord.getStatus() == 1) {
                log.info("充值记录已处理成功，无需重复处理，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            // 4. 校验卡手续费以及更新充值金额
            validateRechargeAmount(miPayCardNotifyResponse, rechargeRecord);

            // 5. 查询或创建回调记录
            CardRechargeCallbackRecord callbackRecord = createOrUpdateCallbackRecord(miPayCardNotifyResponse);

            // 6. 处理充值或者失败
            return handleRechargeStatus(miPayCardNotifyResponse, rechargeRecord, callbackRecord);
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡开通回调处理失败");
        }
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.CardRecharge.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实卡充值记录是否存在
    private CardRechargeRecord validateAndGetRechargeRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardRechargeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardRechargeRecord::getCardCode, response.getCardCode())
                .eq(CardRechargeRecord::getCardId, response.getCardId())
                .eq(CardRechargeRecord::getOrderId, response.getMchOrderId());
        CardRechargeRecord record = cardRechargeRecordService.getOne(queryWrapper);
        Assert.isTrue(record != null, "核实卡充值记录不存在");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 4. 校验卡开通手续费
    private void validateRechargeAmount(MiPayCardNotifyResponse response, CardRechargeRecord record) {
        BigDecimal recharge = (BigDecimal) response.getAmount().get("recharge");
        BigDecimal handleFee = (BigDecimal) response.getAmount().get("handleFee");

        // 申请充值的金额必须等于
        Assert.isTrue(record.getOrderAmount().compareTo(recharge) == 0, "充值到账金额金额不一致");
        Assert.isTrue(record.getOrderFee().compareTo(handleFee) == 0, "充值手续费不一致");
        log.info("卡开通手续费校验通过, 实际手续费: {}", record.getOrderFee());
    }

    // 5. 查询或创建回调记录
    private CardRechargeCallbackRecord createOrUpdateCallbackRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardRechargeCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardRechargeCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardRechargeCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardRechargeCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardRechargeCallbackRecord::getCardId, response.getCardId());

        CardRechargeCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardRechargeCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setCardNo(response.getCardNo());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setMchOrderId(response.getMchOrderId());
            callbackRecord.setRecharge((BigDecimal) response.getAmount().get("recharge"));
            callbackRecord.setActual((BigDecimal) response.getAmount().get("actual"));
            callbackRecord.setHandleFee((BigDecimal) response.getAmount().get("handleFee"));
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setStatus(response.getStatus());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 处理重复通知 ，通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }

    // 处理充值状态
    private boolean handleRechargeStatus(MiPayCardNotifyResponse response, CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        if (rechargeRecord.getStatus() == 0 && CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {
            UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(rechargeRecord.getUserId(), rechargeRecord.getBusinessId());
            // 记录充值流水
            recordTransaction(callbackRecord.getHandleFee(), callbackRecord.getRecharge(), userWalletBalance.getBalance(), callbackRecord.getActual(), userWalletBalance.getUserId(), callbackRecord,rechargeRecord);
            // 修改用户钱包余额
            updateUserWalletBalance(rechargeRecord, callbackRecord);
            // 修改卡余额
            updateCardBalance(response, rechargeRecord, callbackRecord);
            // 修改卡充值状态为成功
            updateRechargeRecordStatus(response, callbackRecord);

            // 更新回调记录
            updateCallbackRecord(callbackRecord, response);

            return true;
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            // 处理失败状态 需要重新发起充值
            handleFailedStatus(callbackRecord);
            return true;
        }

        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    /**
     *
     * @param handleFee 充值手续费
     * @param recharge 充值到账金额
     * @param balance 用户原始余额
     * @param actual 实际扣除金额 （包含手续费）
     * @param userId 用户Id
     * @param callbackRecord 回调记录
     */
    private void recordTransaction(BigDecimal handleFee, BigDecimal recharge, BigDecimal balance, BigDecimal actual, Long userId, CardRechargeCallbackRecord callbackRecord, CardRechargeRecord rechargeRecord) {
        // 计算钱包余额
        BigDecimal finalBalance = balance.subtract(actual);
        // 充值金额 实际到账金额 扣除手续费金额
        UserWalletTransaction userWalletTransaction = UserWalletTransaction.builder()
                .userId(userId)
                .balance(balance)
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .amount(recharge)
                .fee(handleFee)
                .actAmount(recharge)
                .finaBalance(finalBalance)
                .type(TransactionTypeEnum.BALANCE_RECHARGE_TO_CARD.getCode())
                .createTime(LocalDateTime.now())
                .partitionKey(DateUtil.getMonth())
                .businessNumber(callbackRecord.getNotifyId())
                .remark(TransactionTypeEnum.BALANCE_RECHARGE_TO_CARD.getDescription())
                .orderId(rechargeRecord.getOrderId())
                .tradeId(rechargeRecord.getTradeId()).build();
        userWalletTransactionService.save(userWalletTransaction);
        log.info("记录交易记录成功,交易信息为:{}", userWalletTransaction);
    }

    private void updateCardBalance(MiPayCardNotifyResponse response, CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
        // 实际到账
        updateWrapper.eq(Card::getCardId, response.getCardId())
                .setSql("card_amount = card_amount + " + callbackRecord.getRecharge())
                .set(Card::getLocalUpdateTime, LocalDateTime.now());
        cardService.update(updateWrapper);
    }

    // 更新充值记录状态
    private void updateRechargeRecordStatus(MiPayCardNotifyResponse response, CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardRechargeRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardRechargeRecord::getCardId, response.getCardId())
                .set(CardRechargeRecord::getStatus, RechargeRecordStatusEnum.SUCCESS.getKey())
                .set(CardRechargeRecord::getUpdateTime, LocalDateTime.now())
                .set(CardRechargeRecord::getActAmount, callbackRecord.getActual())
                .set(CardRechargeRecord::getFinishTime, LocalDateTime.now());
        boolean isUpdated = cardRechargeRecordService.update(updateWrapper);
        if (!isUpdated) {
            log.warn("充值记录状态更新失败，可能已被其他线程处理，通知ID: {}", response.getNotifyId());
        }
    }

    // 更新用户钱包余额
    private void updateUserWalletBalance(CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<UserWalletBalance> balanceUpdateWrapper = new LambdaUpdateWrapper<>();
        balanceUpdateWrapper.eq(UserWalletBalance::getUserId, rechargeRecord.getUserId())
                .setSql("balance = balance - " + callbackRecord.getActual())
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
        userWalletBalanceService.update(balanceUpdateWrapper);
    }

    // 更新回调记录
    private void updateCallbackRecord(CardRechargeCallbackRecord callbackRecord, MiPayCardNotifyResponse response) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    // 处理失败状态
    private void handleFailedStatus(CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardRechargeCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardRechargeCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .set(CardRechargeCallbackRecord::getUpdateTime, LocalDateTime.now())
                .set(CardRechargeCallbackRecord::getFinishTime, LocalDateTime.now());
        this.update(updateWrapper);
    }
}
