package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.context.MiPayNotifyType;
import com.starchain.dao.CardOpenCallbackRecordMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardOpenCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.enums.CardStatusEnum;
import com.starchain.service.ICardOpenCallbackRecordService;
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
 * @Description
 */
@Slf4j
@Service
public class CardOpenCallbackRecordServiceImpl extends ServiceImpl<CardOpenCallbackRecordMapper, CardOpenCallbackRecord> implements ICardOpenCallbackRecordService {


    @Autowired
    private ICardService cardService;

    /**
     * 卡开通回调
     *
     * @param miPayCardNotifyResponse
     * @return
     */
    @Override
    public Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        try {
            // 1. 校验业务类型
            Assert.isTrue(
                    MiPayNotifyType.CardOpen.getType().equals(miPayCardNotifyResponse.getBusinessType()),
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

            // 3. 校验卡开通手续费
            BigDecimal actual = (BigDecimal) miPayCardNotifyResponse.getAmount().get("actual");
            Assert.isTrue(
                    card.getCardFee().compareTo(actual) == 0,
                    "卡开通手续费不一致"
            );
            log.info("卡开通手续费校验通过, 实际手续费: {}", actual);

            // 4. 查询或创建回调记录
            LambdaQueryWrapper<CardOpenCallbackRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(CardOpenCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            recordQueryWrapper.eq(CardOpenCallbackRecord::getCardCode, miPayCardNotifyResponse.getCardCode());
            recordQueryWrapper.eq(CardOpenCallbackRecord::getBusinessType, miPayCardNotifyResponse.getBusinessType());
            recordQueryWrapper.eq(CardOpenCallbackRecord::getCardId, miPayCardNotifyResponse.getCardId());

            CardOpenCallbackRecord cardOpenCallbackRecord = this.getOne(recordQueryWrapper);
            if (cardOpenCallbackRecord == null) {
                cardOpenCallbackRecord = new CardOpenCallbackRecord();
                cardOpenCallbackRecord.setNotifyId(miPayCardNotifyResponse.getNotifyId());
                cardOpenCallbackRecord.setCardCode(miPayCardNotifyResponse.getCardCode());
                cardOpenCallbackRecord.setBusinessType(miPayCardNotifyResponse.getBusinessType());
                cardOpenCallbackRecord.setCardId(miPayCardNotifyResponse.getCardId());
                cardOpenCallbackRecord.setLocalCreateTime(LocalDateTime.now());
                cardOpenCallbackRecord.setLocalUpdateTime(LocalDateTime.now());
                this.save(cardOpenCallbackRecord);
                log.info("创建新的回调记录, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            } else {
                log.info("回调记录已存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            }

            // 5. 处理回调状态
            if (card.getCardStatus().equals(CardStatusEnum.ACTIVATING.getCardStatus())
                    && card.getStatus() == 0
                    && "SUCCESS".equals(miPayCardNotifyResponse.getStatus())
                    && "CardOpen success".equals(miPayCardNotifyResponse.getStatusDesc())) {
                // 5.1 修改卡状态为开通成功
                card.setCardStatus(CardStatusEnum.NORMAL.getCardStatus());
                card.setStatus(1);
                card.setLocalUpdateTime(LocalDateTime.now());
                card.setFinishTime(LocalDateTime.now());
                cardService.updateById(card);

                // 5.2 更新回调记录
                cardOpenCallbackRecord.setLocalUpdateTime(LocalDateTime.now());
                cardOpenCallbackRecord.setFinishTime(LocalDateTime.now());
                this.updateById(cardOpenCallbackRecord);

                log.info("卡状态更新为开通成功, 卡ID: {}", card.getCardId());
                return true;
            } else if ("FAILED".equals(miPayCardNotifyResponse.getStatus())) {
                // 5.3 处理失败状态
                LambdaUpdateWrapper<CardOpenCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CardOpenCallbackRecord::getNotifyId, cardOpenCallbackRecord.getNotifyId());
                updateWrapper.setSql("retries = retries + 1");
                updateWrapper.set(CardOpenCallbackRecord::getLocalUpdateTime, LocalDateTime.now());
                this.update(updateWrapper);

                log.warn("卡开通失败, 通知ID: {}, 重试次数: {}", cardOpenCallbackRecord.getNotifyId(), cardOpenCallbackRecord.getRetries());
                return false;
            }

            log.info("回调处理完成, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            return true;
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new RuntimeException("卡开通回调处理失败", e);
        }
    }

}
