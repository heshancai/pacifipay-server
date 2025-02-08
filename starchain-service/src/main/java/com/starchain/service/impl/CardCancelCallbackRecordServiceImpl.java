package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.*;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.common.enums.*;
import com.starchain.common.util.DateUtil;
import com.starchain.dao.CardCancelCallbackRecordMapper;
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
 * @Description 销卡通知
 */
@Service
@Slf4j
public class CardCancelCallbackRecordServiceImpl extends ServiceImpl<CardCancelCallbackRecordMapper, CardCancelCallbackRecord>
        implements IMiPayNotifyService, ICardCancelCallbackRecordService {

    @Autowired
    private ICardService cardService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;

    @Autowired
    private ICardCancelRecordService cardCancelRecordService;

    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;

    @Autowired
    private ICardFeeRuleService cardFeeRuleService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean callBack(String callBackJson) {
        MiPayCardNotifyResponse miPayCardNotifyResponse = covertToMiPayCardNotifyResponse(callBackJson);
        try {
            validateBusinessType(miPayCardNotifyResponse);

            CardCancelRecord cardCancelRecord = validateAndGetRecord(miPayCardNotifyResponse);
            if (cardCancelRecord == null) {
                log.info("卡注销记录不存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            Card card = validateCardInfo(miPayCardNotifyResponse);
            if (card == null) {
                log.info("卡信息不存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            if (isCardCancelled(card)) {
                log.info("卡已注销，无需重复处理, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            log.info("卡信息校验通过, 卡ID: {}", miPayCardNotifyResponse.getCardId());

            CardCancelCallbackRecord cardCancelCallbackRecord = saveCallbackRecordIfNotExists(miPayCardNotifyResponse);

            if (isCardCancelSuccess(miPayCardNotifyResponse)) {
                handleCardCancelSuccess(cardCancelRecord, card, cardCancelCallbackRecord);
            } else {
                handleCardCancelFail(cardCancelRecord);
            }
            return true;
        } catch (Exception e) {
            log.error("销卡失败, 错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("卡销毁回调处理失败", e);
        }
    }

    /**
     * 处理销卡失败业务
     * @param cardCancelRecord
     */
    private void handleCardCancelFail(CardCancelRecord cardCancelRecord) {
        UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(cardCancelRecord.getUserId(), cardCancelRecord.getBusinessId());
        CardFeeRule cardFeeRule = getCardFeeRule(cardCancelRecord.getCardCode());

        BigDecimal cancelFee = cardFeeRule.getCancelFee();
        BigDecimal finalBalance = userWalletBalance.getAvaBalance().add(cancelFee);

        // 冻结余额释放 余额增加
        updateUserWalletBalance(cardCancelRecord, cancelFee, cancelFee);

        // 创建用户钱包交易记录
        createUserWalletTransaction(userWalletBalance, cardCancelRecord, cancelFee, finalBalance, TransactionTypeEnum.CARD_CANCEL_FEE);

        // 更新销卡记录状态
        updateCardCancelRecordStatus(cardCancelRecord, CreateStatusEnum.FAILED);
    }

    /**
     * 处理销卡成功业务
     * @param cancelRecord
     * @param card
     * @param callbackRecord
     */
    private void handleCardCancelSuccess(CardCancelRecord cancelRecord, Card card, CardCancelCallbackRecord callbackRecord) {
        UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(cancelRecord.getUserId(), cancelRecord.getBusinessId());
        // 创建用户钱包交易记录
        createUserWalletTransaction(userWalletBalance, cancelRecord, callbackRecord.getReturnAmount(), userWalletBalance.getAvaBalance().add(callbackRecord.getReturnAmount()), TransactionTypeEnum.CARD_CANCEL_RETURN);
        // 更新用户钱包余额和冻结金额
        updateUserWalletBalance(cancelRecord, callbackRecord.getReturnAmount(),BigDecimal.ZERO);
        // 更新卡状态和手续费
        updateCardStatus(card, callbackRecord.getHandleFeeAmount());
        updateCardCancelRecordStatus(cancelRecord, CreateStatusEnum.SUCCESS);
        updateCallbackRecord(callbackRecord);
    }

    private void updateUserWalletBalance(CardCancelRecord cardCancelRecord, BigDecimal finalBalance, BigDecimal cancelFee) {
        LambdaUpdateWrapper<UserWalletBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserWalletBalance::getUserId, cardCancelRecord.getUserId())
                .eq(UserWalletBalance::getBusinessId, cardCancelRecord.getBusinessId())
                .setSql("balance = balance + " + finalBalance)
                .setSql("freeze_balance = freeze_balance - " + cancelFee)
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());

        userWalletBalanceService.update(updateWrapper);
    }

    private void createUserWalletTransaction(UserWalletBalance userWalletBalance, CardCancelRecord cancelRecord, BigDecimal amount, BigDecimal finalBalance, TransactionTypeEnum transactionType) {
        UserWalletTransaction walletTransaction = UserWalletTransaction.builder()
                .userId(userWalletBalance.getUserId())
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .balance(userWalletBalance.getAvaBalance())
                .amount(amount)
                .finaBalance(finalBalance)
                .type(transactionType.getCode())
                .businessNumber(cancelRecord.getCardId())
                .partitionKey(DateUtil.getMonth())
                .remark(transactionType.getDescription())
                .createTime(LocalDateTime.now())
                .build();

        userWalletTransactionService.save(walletTransaction);
        log.info("记录交易流水, 交易信息: {}", walletTransaction);
    }

    private void updateCardStatus(Card card, BigDecimal handleFeeAmount) {
        card.setCardStatus(CardStatusEnum.CANCELLED.getCardStatus());
        card.setLocalUpdateTime(LocalDateTime.now());
        card.setFinishTime(LocalDateTime.now());
        card.setCancelTime(LocalDateTime.now());
        card.setCardAmount(BigDecimal.ZERO);
        card.setHandleFeeAmount(handleFeeAmount);
        cardService.updateById(card);
    }

    private void updateCardCancelRecordStatus(CardCancelRecord cancelRecord, CreateStatusEnum status) {
        cancelRecord.setCreateStatus(status.getCode());
        cancelRecord.setUpdateTime(LocalDateTime.now());
        cancelRecord.setFinishTime(LocalDateTime.now());
        cardCancelRecordService.updateById(cancelRecord);
    }

    private void updateCallbackRecord(CardCancelCallbackRecord callbackRecord) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    private CardFeeRule getCardFeeRule(String cardCode) {
        LambdaQueryWrapper<CardFeeRule> cardFeeRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cardFeeRuleLambdaQueryWrapper.eq(CardFeeRule::getCardCode, cardCode);
        return cardFeeRuleService.getOne(cardFeeRuleLambdaQueryWrapper);
    }

    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.CardCancel.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    private boolean isCardCancelled(Card card) {
        return card.getCardStatus().equals(CardStatusEnum.CANCELLED.getCardStatus());
    }

    private CardCancelRecord validateAndGetRecord(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        LambdaQueryWrapper<CardCancelRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardCancelRecord::getCardCode, miPayCardNotifyResponse.getCardCode())
                .eq(CardCancelRecord::getCardId, miPayCardNotifyResponse.getCardId());
        CardCancelRecord record = cardCancelRecordService.getOne(queryWrapper);
        Assert.isTrue(record != null, "卡注销记录不存在");
        Assert.isTrue(record.getCreateStatus().equals(CreateStatusEnum.CREATING.getCode()), "卡注销记录状态异常");
        log.info("卡注销记录校验通过, 卡ID: {}", record.getCardId());
        return record;
    }

    private Card validateCardInfo(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        LambdaQueryWrapper<Card> cardQueryWrapper = new LambdaQueryWrapper<>();
        cardQueryWrapper.eq(Card::getCardId, miPayCardNotifyResponse.getCardId())
                .eq(Card::getCardNo, miPayCardNotifyResponse.getCardNo())
                .eq(Card::getCardCode, miPayCardNotifyResponse.getCardCode());
        return cardService.getOne(cardQueryWrapper);
    }

    private CardCancelCallbackRecord saveCallbackRecordIfNotExists(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardCancelCallbackRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardCancelCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardCancelCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardCancelCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardCancelCallbackRecord::getCardId, response.getCardId());

        CardCancelCallbackRecord callbackRecord = this.getOne(wrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardCancelCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setReturnAmount((BigDecimal) response.getAmount().get("returnAmount"));
            callbackRecord.setHandleFeeAmount((BigDecimal) response.getAmount().get("handleFeeAmount"));
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setStatus(response.getStatus());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }

    /**
     * 判断卡注销是否成功
     * @param response
     * @return
     */
    private boolean isCardCancelSuccess(MiPayCardNotifyResponse response) {
        return CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus()) && "CardCancel success".equals(response.getStatusDesc());
    }
}

