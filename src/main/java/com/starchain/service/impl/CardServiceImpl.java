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
import com.starchain.enums.CardStatusEnum;
import com.starchain.service.ICardService;
import com.starchain.util.TpyshUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@Slf4j
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements ICardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    /**
     * 查询商户余额
     *
     * @return
     */
    public JSONObject mchInfo() {
        String token = null;
        try {
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET, pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.mchInfo, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        return JSON.parseObject(str);
    }

    @Override
    public Integer checkCardNum(Long cardHolderId, Integer channelId, String cardCode) {
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Card::getCardHolderId, cardHolderId).eq(Card::getCardStatus, CardStatusEnum.NORMAL.getCardStatus()).eq(Card::getCardCode, cardCode);
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
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET, pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 创建卡
        System.out.println("token=>" + token);
        Card card = new Card();
        card.setCardCode(cardHolder.getCardCode());
        card.setSaveOrderId("ORDER789012");
        card.setSaveAmount(new BigDecimal("10.00"));
        card.setTpyshCardHolderId(cardHolder.getTpyshCardHolderId());
        card.setLocalCreateTime(LocalDateTime.now());
        card.setLocalUpdateTime(LocalDateTime.now());
        // 卡状态为激活中
        card.setCardStatus(CardStatusEnum.ACTIVATING.getCardStatus());
        this.save(card);
        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.addCard, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        Card returnCard = JSON.parseObject(str, Card.class);
        BeanUtils.copyProperties(returnCard, card);
        card.setLocalUpdateTime(LocalDateTime.now());
        this.updateById(card);
        log.info("发起创建卡成功:{}", card);
        return card;
    }
}
