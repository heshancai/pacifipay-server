package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.dao.CardRechargeRecordMapper;
import com.starchain.entity.CardRechargeRecord;
import com.starchain.service.ICardRechargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-08
 * @Description 卡充值、卡提现处理类
 */
@Service
@Slf4j
public class CardRechargeRecordServiceImpl extends ServiceImpl<CardRechargeRecordMapper, CardRechargeRecord> implements ICardRechargeRecordService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

//    /**
//     * 卡充值
//     *
//     * @param cardDto
//     * @return
//     */
//    public Card applyRecharge(CardDto cardDto) {
//        String token = null;
//        try {
//            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
//            // 创建卡
//            System.out.println("token=>" + token);
//
//            Card card = new Card();
//            card.setCardCode(cardHolder.getCardCode());
//            card.setCardHolderId(cardHolder.getId());
//            //  生成一个唯一的订单号
//            card.setSaveOrderId(OrderIdGenerator.generateOrderId("", "", 6));
//            // 太平洋的持卡人唯一值
//            card.setTpyshCardHolderId(cardHolder.getTpyshCardHolderId());
//            card.setCardStatus(CardStatusEnum.ACTIVATING.getCardStatus());
//            card.setStatus(0);
//            card.setLocalCreateTime(LocalDateTime.now());
//            card.setLocalUpdateTime(LocalDateTime.now());
//            card.setSaveAmount(BigDecimal.ZERO);
//            this.save(card);
//            // 卡状态为激活中
//            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.addCard, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
//            log.info("返回的数据：{}", str);
//            Card returnCard = JSON.parseObject(str, Card.class);
//            BeanUtils.copyProperties(returnCard, card);
//            card.setLocalUpdateTime(LocalDateTime.now());
//            this.updateById(card);
//            log.info("发起创建卡成功:{}", card);
//            return card;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 卡提现
//     *
//     * @param cardDto
//     * @return
//     */
//    public Card applyWithdraw(CardDto cardDto) {
//        String token = null;
//        try {
//            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
//            // 创建卡
//            System.out.println("token=>" + token);
//
//            Card card = new Card();
//            card.setCardCode(cardHolder.getCardCode());
//            card.setCardHolderId(cardHolder.getId());
//            //  生成一个唯一的订单号
//            card.setSaveOrderId(OrderIdGenerator.generateOrderId("", "", 6));
//            // 太平洋的持卡人唯一值
//            card.setTpyshCardHolderId(cardHolder.getTpyshCardHolderId());
//            card.setCardStatus(CardStatusEnum.ACTIVATING.getCardStatus());
//            card.setStatus(0);
//            card.setLocalCreateTime(LocalDateTime.now());
//            card.setLocalUpdateTime(LocalDateTime.now());
//            card.setSaveAmount(BigDecimal.ZERO);
//            this.save(card);
//            // 卡状态为激活中
//            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.addCard, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
//            log.info("返回的数据：{}", str);
//            Card returnCard = JSON.parseObject(str, Card.class);
//            BeanUtils.copyProperties(returnCard, card);
//            card.setLocalUpdateTime(LocalDateTime.now());
//            this.updateById(card);
//            log.info("发起创建卡成功:{}", card);
//            return card;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
