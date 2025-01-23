package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.CardCancelCallbackRecordMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardCancelCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.enums.CardStatusEnum;
import com.starchain.enums.MiPayNotifyType;
import com.starchain.service.ICardCancelCallbackRecordService;
import com.starchain.service.ICardService;
import com.starchain.service.IMiPayNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class CardCancelCallbackRecordServiceImpl extends ServiceImpl<CardCancelCallbackRecordMapper, CardCancelCallbackRecord> implements IMiPayNotifyService, ICardCancelCallbackRecordService {

    @Autowired
    private ICardService cardService;

    /**
     * 申请销卡回调
     *
     * @param callBackJson
     * @return
     */
    @Override
    public Boolean callBack(String callBackJson) {

        MiPayCardNotifyResponse miPayCardNotifyResponse = this.covertToMiPayCardNotifyResponse(callBackJson);

        try {
            // 1. 校验业务类型
            Assert.isTrue(
                    MiPayNotifyType.CardCancel.getType().equals(miPayCardNotifyResponse.getBusinessType()),
                    "回调业务类型与接口调用不匹配"
            );
            log.info("回调业务类型校验通过: {}", miPayCardNotifyResponse.getBusinessType());

            // 2. 检查卡信息是否存在
            LambdaQueryWrapper<Card> cardQueryWrapper = new LambdaQueryWrapper<>();
            cardQueryWrapper.eq(Card::getCardId, miPayCardNotifyResponse.getCardId());
            cardQueryWrapper.eq(Card::getCardNo, miPayCardNotifyResponse.getCardNo());
            cardQueryWrapper.eq(Card::getCardCode, miPayCardNotifyResponse.getCardCode());
            Card card = cardService.getOne(cardQueryWrapper);
            Assert.isTrue(card != null, "卡信息不存在");
            log.info("卡信息校验通过, 卡ID: {}", miPayCardNotifyResponse.getCardId());

            // 4. 查询或创建回调记录
            LambdaQueryWrapper<CardCancelCallbackRecord> cancelCallbackRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cancelCallbackRecordLambdaQueryWrapper.eq(CardCancelCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            cancelCallbackRecordLambdaQueryWrapper.eq(CardCancelCallbackRecord::getCardCode, miPayCardNotifyResponse.getCardCode());
            cancelCallbackRecordLambdaQueryWrapper.eq(CardCancelCallbackRecord::getBusinessType, miPayCardNotifyResponse.getBusinessType());
            cancelCallbackRecordLambdaQueryWrapper.eq(CardCancelCallbackRecord::getCardId, miPayCardNotifyResponse.getCardId());

            CardCancelCallbackRecord cardCancelCallbackRecord = this.getOne(cancelCallbackRecordLambdaQueryWrapper);
            if (cardCancelCallbackRecord == null) {
                cardCancelCallbackRecord = new CardCancelCallbackRecord();
                cardCancelCallbackRecord.setNotifyId(miPayCardNotifyResponse.getNotifyId());
                cardCancelCallbackRecord.setCardCode(miPayCardNotifyResponse.getCardCode());
                cardCancelCallbackRecord.setBusinessType(miPayCardNotifyResponse.getBusinessType());
                cardCancelCallbackRecord.setReturnAmount((BigDecimal) miPayCardNotifyResponse.getAmount().get("returnAmount"));
                cardCancelCallbackRecord.setHandleFeeAmount((BigDecimal) miPayCardNotifyResponse.getAmount().get("handleFeeAmount"));
                cardCancelCallbackRecord.setCardId(miPayCardNotifyResponse.getCardId());
                cardCancelCallbackRecord.setCreateTime(LocalDateTime.now());
                cardCancelCallbackRecord.setUpdateTime(LocalDateTime.now());
                this.save(cardCancelCallbackRecord);
                log.info("创建新的回调记录, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            } else {
                log.info("回调记录已存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            }

            // 5. 处理回调状态
            if (card.getCardStatus().equals(CardStatusEnum.NORMAL.getCardStatus())
                    && "SUCCESS".equals(miPayCardNotifyResponse.getStatus())
                    && "CardCancel success".equals(miPayCardNotifyResponse.getStatusDesc())) {
                // 5.1 销卡成功
                card.setCardStatus(CardStatusEnum.NORMAL.getCardStatus());
                card.setCreateStatus(3);
                card.setLocalUpdateTime(LocalDateTime.now());
                card.setFinishTime(LocalDateTime.now());
                card.setCancelTime(LocalDateTime.now());
                card.setHandleFeeAmount(cardCancelCallbackRecord.getHandleFeeAmount());
                cardService.updateById(card);

                // 5.2 更新回调记录
                cardCancelCallbackRecord.setUpdateTime(LocalDateTime.now());
                cardCancelCallbackRecord.setFinishTime(LocalDateTime.now());
                this.updateById(cardCancelCallbackRecord);

                log.info("卡状态更新为开通成功, 卡ID: {}", card.getCardId());
                return true;
            } else if ("FAILED".equals(miPayCardNotifyResponse.getStatus())) {
                // 5.3 处理失败状态
                LambdaUpdateWrapper<CardCancelCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CardCancelCallbackRecord::getNotifyId, cardCancelCallbackRecord.getNotifyId());
                updateWrapper.setSql("retries = retries + 1");
                updateWrapper.set(CardCancelCallbackRecord::getUpdateTime, LocalDateTime.now());
                this.update(updateWrapper);

                log.warn("卡开通失败, 通知ID: {}, 重试次数: {}", cardCancelCallbackRecord.getNotifyId(), cardCancelCallbackRecord.getRetries());
                return false;
            }

            log.info("回调处理完成, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            return true;
        } catch (Exception e) {
            log.error("销卡失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new RuntimeException("卡开通回调处理失败", e);
        }
    }
}
