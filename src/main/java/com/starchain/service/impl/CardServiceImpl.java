package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.context.MiPayNotifyType;
import com.starchain.dao.CardMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.CardOpenCallbackRecord;
import com.starchain.entity.dto.CardDto;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.enums.CardStatusEnum;
import com.starchain.service.ICardOpenCallbackRecordService;
import com.starchain.service.ICardService;
import com.starchain.util.HttpUtils;
import com.starchain.util.OrderIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@Slf4j
@Service("cardServiceImpl") // 注意这里的 Bean 名称与 MiPayNotifyType 中的 serviceName 一致
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements ICardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private ICardOpenCallbackRecordService cardOpenCallbackRecordService;

    /**
     * 查询商户余额
     *
     * @return
     */
    public JSONObject mchInfo() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.mchInfo, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        return JSON.parseObject(str);
    }

    @Override
    public Integer checkCardNum(Long cardHolderId, Integer channelId, String cardCode, String tpyshCardHolderId) {
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Card::getCardHolderId, cardHolderId).eq(Card::getCardCode, cardCode).eq(Card::getTpyshCardHolderId, tpyshCardHolderId);
        return this.count(lambdaQueryWrapper);
    }


    /**
     * 创建持卡人 成功 预先存部分虚拟卡数据
     *
     * @param cardHolder
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Card addCard(CardHolder cardHolder) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            // 创建卡
            System.out.println("token=>" + token);

            Card card = new Card();
            card.setCardCode(cardHolder.getCardCode());
            card.setCardHolderId(cardHolder.getId());
            //  生成一个唯一的订单号
            card.setSaveOrderId(OrderIdGenerator.generateOrderId("", "", 6));
            // 太平洋的持卡人唯一值
            card.setTpyshCardHolderId(cardHolder.getTpyshCardHolderId());
            card.setCardStatus(CardStatusEnum.CANCELLED.getCardStatus());
            card.setStatus(3);
            card.setLocalCreateTime(LocalDateTime.now());
            card.setLocalUpdateTime(LocalDateTime.now());
            card.setSaveAmount(BigDecimal.ZERO);
            this.save(card);
            // 卡状态为激活中
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.addCard, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (CardStatusEnum.NORMAL.getCardStatus().equals(returnCard.getCardStatus())) {
                returnCard.setStatus(1);
            } else {
                returnCard.setStatus(card.getStatus());
            }
            returnCard.setId(card.getId());
            returnCard.setLocalCreateTime(card.getLocalCreateTime());
            returnCard.setLocalUpdateTime(LocalDateTime.now());
            this.updateById(returnCard);
            log.info("发起创建卡成功:{}", returnCard);
            return card;
        } catch (Exception e) {
            log.error("服务异常", e);
            throw new RuntimeException(e);
        }
    }


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
            Card card = this.getOne(cardQueryWrapper);
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

            CardOpenCallbackRecord cardOpenCallbackRecord = cardOpenCallbackRecordService.getOne(recordQueryWrapper);
            if (cardOpenCallbackRecord == null) {
                cardOpenCallbackRecord = new CardOpenCallbackRecord();
                cardOpenCallbackRecord.setNotifyId(miPayCardNotifyResponse.getNotifyId());
                cardOpenCallbackRecord.setCardCode(miPayCardNotifyResponse.getCardCode());
                cardOpenCallbackRecord.setBusinessType(miPayCardNotifyResponse.getBusinessType());
                cardOpenCallbackRecord.setCardId(miPayCardNotifyResponse.getCardId());
                cardOpenCallbackRecord.setLocalCreateTime(LocalDateTime.now());
                cardOpenCallbackRecord.setLocalUpdateTime(LocalDateTime.now());
                cardOpenCallbackRecordService.save(cardOpenCallbackRecord);
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
                this.updateById(card);

                // 5.2 更新回调记录
                cardOpenCallbackRecord.setLocalUpdateTime(LocalDateTime.now());
                cardOpenCallbackRecord.setFinishTime(LocalDateTime.now());
                cardOpenCallbackRecordService.updateById(cardOpenCallbackRecord);

                log.info("卡状态更新为开通成功, 卡ID: {}", card.getCardId());
                return true;
            } else if ("FAILED".equals(miPayCardNotifyResponse.getStatus())) {
                // 5.3 处理失败状态
                LambdaUpdateWrapper<CardOpenCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CardOpenCallbackRecord::getNotifyId, cardOpenCallbackRecord.getNotifyId());
                updateWrapper.setSql("retries = retries + 1");
                updateWrapper.set(CardOpenCallbackRecord::getLocalUpdateTime, LocalDateTime.now());
                cardOpenCallbackRecordService.update(updateWrapper);

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

    /**
     * 申请销卡
     *
     * @param
     * @param
     * @return
     */
    @Override
    public Boolean deleteCard(CardDto cardDto) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.deleteCard, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            CardDto returnCard = JSON.parseObject(str, CardDto.class);
            if (returnCard != null) return true;
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return false;
    }

    @Override
    public Card changeCard(CardDto cardDto) {
        return null;
    }
}
