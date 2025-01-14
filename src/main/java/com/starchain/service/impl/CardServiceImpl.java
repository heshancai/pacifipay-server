package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.dao.CardMapper;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.CardRechargeRecord;
import com.starchain.entity.dto.CardDto;
import com.starchain.enums.CardStatusEnum;
import com.starchain.service.*;
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
    private ICardHolderService cardHolderService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ICardRechargeRecordService cardRechargeRecordService;

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
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.MCH_INFO, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        return JSON.parseObject(str);
    }

    @Override
    public Integer checkCardNum(Long cardHolderId, Long channelId, String cardCode, String tpyshCardHolderId) {
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Card::getCardCode, cardCode).eq(Card::getTpyshCardHolderId, tpyshCardHolderId);
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
            //  生成一个唯一的订单号
            card.setSaveOrderId(OrderIdGenerator.generateOrderId("", "", 6));
            // 太平洋的持卡人唯一值
            card.setTpyshCardHolderId(cardHolder.getTpyshCardHolderId());
            card.setCardStatus(CardStatusEnum.ACTIVATING.getCardStatus());
            card.setCreateStatus(0);
            card.setLocalCreateTime(LocalDateTime.now());
            card.setLocalUpdateTime(LocalDateTime.now());
            card.setSaveAmount(BigDecimal.ZERO);
            this.save(card);
            // 卡状态为激活中
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.ADD_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (CardStatusEnum.NORMAL.getCardStatus().equals(returnCard.getCardStatus())) {
                returnCard.setCreateStatus(1);
            } else {
                returnCard.setCreateStatus(card.getCreateStatus());
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
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.DELETE_CARD, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            CardDto returnCard = JSON.parseObject(str, CardDto.class);
            if (returnCard != null) return true;
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return false;
    }

    /**
     * 申请换卡 暂时没用
     *
     * @param card
     * @return
     */
    @Override
    public Card changeCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.DELETE_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
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
        String token = null;
        try {

            // 持卡人校验
            LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(CardHolder::getUserId, cardDto.getUserId());
            lambdaQueryWrapper.eq(CardHolder::getCardCode, cardDto.getCardCode());
            lambdaQueryWrapper.eq(CardHolder::getTpyshCardHolderId, cardDto.getTpyshCardHolderId());
            lambdaQueryWrapper.eq(CardHolder::getChannelId, cardDto.getChannelId());
            CardHolder cardHolder = cardHolderService.getOne(lambdaQueryWrapper);
            Assert.notNull(cardHolder, "持卡人不存在");

            // 卡校验 必须为使用在才能充值
            LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cardLambdaQueryWrapper.eq(Card::getCardId, cardDto.getCardId());
            cardLambdaQueryWrapper.eq(Card::getTpyshCardHolderId, cardDto.getTpyshCardHolderId());
            cardLambdaQueryWrapper.eq(Card::getCreateStatus, 1);
            cardLambdaQueryWrapper.eq(Card::getCardStatus, CardStatusEnum.NORMAL.getCardStatus());
            Card card = this.getOne(cardLambdaQueryWrapper);
            Assert.notNull(card, "卡不存在");

            // 余额必须大于 0 且必须大于输入的金额
            BigDecimal orderAmount = cardDto.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
            userWalletBalanceService.checkUserBalance(cardDto.getUserId(), cardDto.getChannelId(), orderAmount);
            cardDto.setOrderAmount(orderAmount);
            // 封装传递参数
            cardDto.setOrderId(String.valueOf(idWorker.nextId()));
            // 条件通过 进行充值
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.APPLY_RECHARGE, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);

            CardRechargeRecord cardRechargeRecord = JSON.parseObject(str, CardRechargeRecord.class);
            cardRechargeRecord.setStatus(0);
            cardRechargeRecord.setUserId(cardDto.getUserId());
            cardRechargeRecord.setTpyshCardHolderId(cardDto.getTpyshCardHolderId());
            cardRechargeRecord.setChannelId(cardDto.getChannelId());
            cardRechargeRecord.setCreateTime(LocalDateTime.now());
            cardRechargeRecord.setUpdateTime(LocalDateTime.now());
            // 处理成功 等待回调结果
            cardRechargeRecordService.save(cardRechargeRecord);
            return cardRechargeRecord;
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return null;
    }

    // 锁卡
    @Override
    public Boolean lockCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.LOCK_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (returnCard != null) {
                LambdaUpdateWrapper<Card> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Card::getCardId, returnCard.getCardId());
                lambdaUpdateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
                lambdaUpdateWrapper.eq(Card::getCardCode, returnCard.getCardNo());
                lambdaUpdateWrapper.set(Card::getCardStatus, CardStatusEnum.FREEZING.getCardStatus());
                return this.update(lambdaUpdateWrapper);
            }
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return false;
    }

    @Override
    public Boolean unlockCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.UNLOCK_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (returnCard != null) {
                LambdaUpdateWrapper<Card> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Card::getCardId, returnCard.getCardId());
                lambdaUpdateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
                lambdaUpdateWrapper.eq(Card::getCardCode, returnCard.getCardNo());
                lambdaUpdateWrapper.set(Card::getCardStatus, CardStatusEnum.UNACTIVATED.getCardStatus());
                return this.update(lambdaUpdateWrapper);
            }
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return false;
    }
}
