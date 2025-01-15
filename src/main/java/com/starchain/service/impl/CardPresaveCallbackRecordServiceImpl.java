package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.context.MiPayNotifyType;
import com.starchain.dao.CardPresaveCallbackRecordMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardPresaveCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.enums.CardStatusDescEnum;
import com.starchain.enums.CreateStatusEnum;
import com.starchain.exception.StarChainException;
import com.starchain.service.ICardPresaveCallbackRecordService;
import com.starchain.service.ICardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description 卡预存通知： 创建卡同时会进行预存 并且进行回调
 */
@Service
@Slf4j
public class CardPresaveCallbackRecordServiceImpl extends ServiceImpl<CardPresaveCallbackRecordMapper, CardPresaveCallbackRecord> implements ICardPresaveCallbackRecordService {
    @Autowired
    private ICardService cardService;

    @Override
    public Boolean callBack(String callBackJson) {

        MiPayCardNotifyResponse miPayCardNotifyResponse = this.covertToMiPayCardNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 核实卡开通记录是否存在 卡开通成功才会处理预存
            Card card = validateAndGetRechargeRecord(miPayCardNotifyResponse);


            LambdaQueryWrapper<CardPresaveCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CardPresaveCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            CardPresaveCallbackRecord callbackRecord = this.getOne(queryWrapper);
            // 3. 检查是否已经处理成功（幂等性）
            if (callbackRecord != null && CardStatusDescEnum.SUCCESS.getDescription().equals(callbackRecord.getStatusDesc())) {
                log.info("卡预存回调记录已处理成功，无需重复处理，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }
            // 4. 校验卡开预存费用是否一致
            validateRechargeAmount(miPayCardNotifyResponse, card);

            // 5. 查询或创建回调记录
            CardPresaveCallbackRecord cardPresaveCallbackRecord = createOrUpdateCallbackRecord(miPayCardNotifyResponse);

            // 6. 处理开卡预存
            return handleRechargeStatus(miPayCardNotifyResponse, cardPresaveCallbackRecord);
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡开通回调处理失败");
        }
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.Presave.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实卡记录是否存在
    private Card validateAndGetRechargeRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getCardCode, response.getCardCode())
                .eq(Card::getCardId, response.getCardId())
                .eq(Card::getSaveOrderId, response.getMchOrderId());
        Card record = cardService.getOne(queryWrapper);
        Assert.notNull(record, "核实卡充值记录不存在");
        Assert.isTrue(record.getCreateStatus().equals(CreateStatusEnum.SUCCESS.getCode()), "卡未创建完成,忽略预存回调信息");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 4. 校验卡预存费用
    private void validateRechargeAmount(MiPayCardNotifyResponse response, Card record) {
        BigDecimal actual = (BigDecimal) response.getAmount().get("actual");
        Assert.isTrue(record.getSaveAmount().compareTo(actual) == 0, "预存款实际扣除金额不一致");
        log.info("卡预存费校验通过, 实际手续费: {}", actual);
    }

    // 5. 查询或创建回调记录
    private CardPresaveCallbackRecord createOrUpdateCallbackRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardPresaveCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardPresaveCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardPresaveCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardPresaveCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardPresaveCallbackRecord::getCardId, response.getCardId());

        CardPresaveCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardPresaveCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setCardNo(response.getCardNo());
            callbackRecord.setMchOrderId(response.getMchOrderId());
            callbackRecord.setActual((BigDecimal) response.getAmount().get("actual"));
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

    // 处理回调记录状态
    private boolean handleRechargeStatus(MiPayCardNotifyResponse response, CardPresaveCallbackRecord rechargeRecord) {
        if (CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {

            // 更新回调记录
            LambdaUpdateWrapper<CardPresaveCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CardPresaveCallbackRecord::getNotifyId, response.getNotifyId())
                    .set(CardPresaveCallbackRecord::getStatusDesc, response.getStatusDesc())
                    .set(CardPresaveCallbackRecord::getStatus, response.getStatus())
                    .set(CardPresaveCallbackRecord::getUpdateTime, LocalDateTime.now());
            return this.update(updateWrapper);
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            // 处理失败状态
            handleFailedStatus(rechargeRecord);
            return false;
        }
        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    // 处理失败状态
    private void handleFailedStatus(CardPresaveCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardPresaveCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardPresaveCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .setSql("retries = retries + 1")
                .set(CardPresaveCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }
}