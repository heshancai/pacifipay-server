package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.dao.CardMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.CardRechargeRecord;
import com.starchain.entity.dto.CardDto;
import com.starchain.enums.CardStatusEnum;
import com.starchain.service.ICardHolderService;
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

    @Autowired
    private ICardHolderService cardHolderService;

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

    /**
     * 申请换卡
     *
     * @param card
     * @return
     */
    @Override
    public Card changeCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.deleteCard, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            CardDto returnCard = JSON.parseObject(str, CardDto.class);
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return null;
    }


    /**
     * 卡充值
     *
     * @param cardDto
     * @return
     */
    @Override
    public CardRechargeRecord applyRecharge(CardDto cardDto) {
        // 持卡人校验
        LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardHolder::getUserId, cardDto.getUserId());
        lambdaQueryWrapper.eq(CardHolder::getCardCode, cardDto.getCardCode());
        lambdaQueryWrapper.eq(CardHolder::getTpyshCardHolderId, cardDto.getTpyshCardHolderId());
        lambdaQueryWrapper.eq(CardHolder::getChannelId, cardDto.getChannelId());
        CardHolder cardHolder = cardHolderService.getOne(lambdaQueryWrapper);
        Assert.notNull(cardHolder, "持卡人不存在");

        // 卡校验
        LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cardLambdaQueryWrapper.eq(Card::getCardId, cardDto.getCardId());
        cardLambdaQueryWrapper.eq(Card::getTpyshCardHolderId, cardDto.getTpyshCardHolderId());
        Card card = this.getOne(cardLambdaQueryWrapper);
        Assert.notNull(card, "卡不存在");

        // 核实用户余额


        return null;
    }
}
