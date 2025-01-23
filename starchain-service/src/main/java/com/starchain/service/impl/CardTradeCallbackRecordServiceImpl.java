package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.CardFeeDetailCallbackRecord;
import com.starchain.common.entity.CardTradeCallbackRecord;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.common.enums.CardStatusDescEnum;
import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.common.exception.StarChainException;
import com.starchain.dao.CardTradeCallbackRecordMapper;
import com.starchain.service.ICardFeeDetailCallbackRecordService;
import com.starchain.service.ICardTradeCallbackRecordService;
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
 * @Description 卡流水通知
 */
@Service
@Slf4j
public class CardTradeCallbackRecordServiceImpl extends ServiceImpl<CardTradeCallbackRecordMapper, CardTradeCallbackRecord> implements ICardTradeCallbackRecordService {


    @Autowired
    private ICardFeeDetailCallbackRecordService cardFeeDetailCallbackRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean callBack(String callBackJson) {

        MiPayCardNotifyResponse miPayCardNotifyResponse = this.covertToMiPayCardNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 检查是否已经处理成功（幂等性）
            LambdaQueryWrapper<CardTradeCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CardTradeCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            CardTradeCallbackRecord callbackRecord = this.getOne(queryWrapper);
            if (callbackRecord != null && CardStatusDescEnum.SUCCESS.getDescription().equals(callbackRecord.getStatusDesc())) {
                log.info("卡流水回调记录已处理成功，无需重复处理，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            // 3. 查询或创建回调记录
            CardTradeCallbackRecord cardTradeCallbackRecord = createOrUpdateCallbackRecord(miPayCardNotifyResponse);

            // 4. 处理卡流水状态
            return handleTradeStatus(miPayCardNotifyResponse, cardTradeCallbackRecord);
        } catch (Exception e) {
            log.error("卡流水回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡流水回调处理失败");
        }
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.CardTrade.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 3. 查询或创建回调记录
    private CardTradeCallbackRecord createOrUpdateCallbackRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardTradeCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardTradeCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardTradeCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardTradeCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardTradeCallbackRecord::getCardId, response.getCardId());

        CardTradeCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardTradeCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setTradeTime(response.getTradeTime());
            callbackRecord.setTradeType(response.getTradeType());
            callbackRecord.setTradeId(response.getTradeId());
            callbackRecord.setOriginalTradeId(response.getOriginalTradeId());
            callbackRecord.setBalance(response.getBalance());
            callbackRecord.setBalance(response.getBalance());
            callbackRecord.setMerchantName(response.getMerchantName());
            callbackRecord.setStatus(response.getStatus());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setFee((BigDecimal) response.getAmount().get("fee"));
            callbackRecord.setTrade((BigDecimal) response.getAmount().get("trade"));
            callbackRecord.setTradeCurrency(String.valueOf(response.getAmount().get("tradeCurrency")));
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            JSONObject feeDetailMap = (JSONObject) response.getAmount().get("feeDetail");
            BigDecimal gatewayFee = (BigDecimal) feeDetailMap.get("gatewayFee");
            BigDecimal verifyFee = (BigDecimal) feeDetailMap.get("verifyFee");
            BigDecimal voidFee = (BigDecimal) feeDetailMap.get("voidFee");
            BigDecimal refundFee = (BigDecimal) feeDetailMap.get("refundFee");
            BigDecimal authFailFee = (BigDecimal) feeDetailMap.get("authFailFee");
            BigDecimal authSuccessFee = (BigDecimal) feeDetailMap.get("authSuccessFee");
            BigDecimal authLittleFee = (BigDecimal) feeDetailMap.get("authLittleFee");
            BigDecimal authBorderFee = (BigDecimal) feeDetailMap.get("authBorderFee");
            CardFeeDetailCallbackRecord cardFeeDetailCallbackRecord = new CardFeeDetailCallbackRecord();
            cardFeeDetailCallbackRecord.setTransactionId(callbackRecord.getId());
            cardFeeDetailCallbackRecord.setGatewayFee(gatewayFee);
            cardFeeDetailCallbackRecord.setVerifyFee(verifyFee);
            cardFeeDetailCallbackRecord.setVoidFee(voidFee);
            cardFeeDetailCallbackRecord.setRefundFee(refundFee);
            cardFeeDetailCallbackRecord.setAuthFailFee(authFailFee);
            cardFeeDetailCallbackRecord.setAuthSuccessFee(authSuccessFee);
            cardFeeDetailCallbackRecord.setAuthLittleFee(authLittleFee);
            cardFeeDetailCallbackRecord.setAuthBorderFee(authBorderFee);
            cardFeeDetailCallbackRecord.setCreateTime(LocalDateTime.now());
            cardFeeDetailCallbackRecord.setUpdateTime(LocalDateTime.now());
            cardFeeDetailCallbackRecordService.save(cardFeeDetailCallbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 处理重复通知 ，通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }

    // 4. 处理卡流水状态
    private boolean handleTradeStatus(MiPayCardNotifyResponse response, CardTradeCallbackRecord tradeRecord) {
        if (CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {

            // 更新回调记录
            LambdaUpdateWrapper<CardTradeCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CardTradeCallbackRecord::getNotifyId, response.getNotifyId())
                    .set(CardTradeCallbackRecord::getStatusDesc, response.getStatusDesc())
                    .set(CardTradeCallbackRecord::getFinishTime, LocalDateTime.now())
                    .set(CardTradeCallbackRecord::getUpdateTime, LocalDateTime.now());

            LambdaUpdateWrapper<CardFeeDetailCallbackRecord> callbackRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            callbackRecordLambdaUpdateWrapper.eq(CardFeeDetailCallbackRecord::getTransactionId, tradeRecord.getId())
                    .set(CardFeeDetailCallbackRecord::getFinishTime, LocalDateTime.now())
                    .set(CardFeeDetailCallbackRecord::getUpdateTime, LocalDateTime.now());
            cardFeeDetailCallbackRecordService.update(callbackRecordLambdaUpdateWrapper);
            return this.update(updateWrapper);
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            // 处理失败状态
            handleFailedStatus(tradeRecord);
            return true;
        }
        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    // 处理失败状态
    private void handleFailedStatus(CardTradeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardTradeCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardTradeCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .setSql("retries = retries + 1")
                .set(CardTradeCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }
}
