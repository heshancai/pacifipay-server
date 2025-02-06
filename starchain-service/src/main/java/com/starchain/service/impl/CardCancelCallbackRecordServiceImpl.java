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

    /**
     * 申请销卡回调
     *
     * @param callBackJson 回调数据
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean callBack(String callBackJson) {
        MiPayCardNotifyResponse miPayCardNotifyResponse = covertToMiPayCardNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 校验并获取卡注销记录
            CardCancelRecord cardCancelRecord = validateAndGetRecord(miPayCardNotifyResponse);
            if (cardCancelRecord == null) {
                log.info("卡注销记录不存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true; // 如果注销记录不存在，则直接返回
            }

            // 3. 校验卡信息是否存在
            Card card = validateCardInfo(miPayCardNotifyResponse);
            if (card == null) {
                log.info("卡信息不存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true; // 如果卡信息不存在，则直接返回
            }

            // 4. 防止重复处理销卡请求
            if (isCardCancelled(card)) {
                log.info("卡已注销，无需重复处理, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            log.info("卡信息校验通过, 卡ID: {}", miPayCardNotifyResponse.getCardId());

            // 5. 保存回调记录
            CardCancelCallbackRecord cardCancelCallbackRecord = saveCallbackRecordIfNotExists(miPayCardNotifyResponse);

            // 6. 处理销卡成功逻辑
            if (isCardCancelSuccess(miPayCardNotifyResponse)) {
                handleCardCancelSuccess(cardCancelRecord, card, cardCancelCallbackRecord);
                return true;
            } else {
                // 销卡失败 需要用户端重新进行发起
                log.warn("销卡失败，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }
        } catch (Exception e) {
            log.error("销卡失败, 错误信息: {}", e.getMessage(), e);
            throw new RuntimeException("卡销毁回调处理失败", e);
        }
    }

    // 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.CardCancel.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }


    // 判断卡是否已经注销
    private boolean isCardCancelled(Card card) {
        return card.getCardStatus().equals(CardStatusEnum.CANCELLED.getCardStatus());
    }

    private CardCancelRecord validateAndGetRecord(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        LambdaQueryWrapper<CardCancelRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardCancelRecord::getCardCode, miPayCardNotifyResponse.getCardCode())
                .eq(CardCancelRecord::getCardId, miPayCardNotifyResponse.getCardId());
        CardCancelRecord record = cardCancelRecordService.getOne(queryWrapper);
        if (record == null) {
            log.info("卡注销记录不存在, 卡号: {}, 卡ID: {}", miPayCardNotifyResponse.getCardNo(), miPayCardNotifyResponse.getCardId());
        }
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
        Card card = cardService.getOne(cardQueryWrapper);
        if (card == null) {
            log.info("卡信息不存在, 卡号: {}, 卡ID: {}", miPayCardNotifyResponse.getCardNo(), miPayCardNotifyResponse.getCardId());
        }
        return card;
    }

    // 保存回调记录
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

    // 判断销卡是否成功
    private boolean isCardCancelSuccess(MiPayCardNotifyResponse response) {
        return CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus()) && "CardCancel success".equals(response.getStatusDesc());
    }

    // 处理销卡成功逻辑
    private void handleCardCancelSuccess(CardCancelRecord cancelRecord, Card card, CardCancelCallbackRecord callbackRecord) {
        // 1. 生成用户钱包流水
        UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(cancelRecord.getUserId(), cancelRecord.getBusinessId());
        createUserWalletTransaction(userWalletBalance, cancelRecord, callbackRecord);

        // 2. 更新钱包余额
        updateUserWalletBalance(cancelRecord, callbackRecord.getReturnAmount());

        // 3. 更新卡状态为销卡 作废并且余额置零
        card.setCardStatus(CardStatusEnum.CANCELLED.getCardStatus());
        card.setLocalUpdateTime(LocalDateTime.now());
        card.setFinishTime(LocalDateTime.now());
        card.setCancelTime(LocalDateTime.now());
        card.setCardAmount(BigDecimal.ZERO);
        card.setHandleFeeAmount(callbackRecord.getHandleFeeAmount());
        cardService.updateById(card);

        // 4. 更新回调记录
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);

        log.info("卡状态更新为销卡成功, 卡ID: {}", card.getCardId());
    }

    // 生成用户钱包流水
    private void createUserWalletTransaction(UserWalletBalance userWalletBalance, CardCancelRecord cancelRecord, CardCancelCallbackRecord callbackRecord) {
        BigDecimal returnAmount = callbackRecord.getReturnAmount();
        BigDecimal handleFeeAmount = callbackRecord.getHandleFeeAmount();
        BigDecimal finalBalance = userWalletBalance.getBalance().add(returnAmount);

        UserWalletTransaction walletTransaction = UserWalletTransaction.builder()
                .userId(userWalletBalance.getUserId())
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .balance(userWalletBalance.getBalance())
                .amount(returnAmount)
                .fee(handleFeeAmount)
                .actAmount(returnAmount)
                .finaBalance(finalBalance)
                .type(TransactionTypeEnum.CANCEL_CARD.getCode())
                .businessNumber(callbackRecord.getNotifyId())
                .partitionKey(DateUtil.getMonth())
                .remark(TransactionTypeEnum.CANCEL_CARD.getDescription())
                .createTime(LocalDateTime.now())
                .build();

        userWalletTransactionService.save(walletTransaction);
        log.info("记录交易流水, 交易信息: {}", walletTransaction);
    }

    // 更新用户钱包余额
    private void updateUserWalletBalance(CardCancelRecord cancelRecord, BigDecimal returnAmount) {
        LambdaUpdateWrapper<UserWalletBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserWalletBalance::getUserId, cancelRecord.getUserId())
                .eq(UserWalletBalance::getBusinessId, cancelRecord.getBusinessId())
                .setSql("balance = balance + " + returnAmount)
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());

        userWalletBalanceService.update(updateWrapper);
    }
}

