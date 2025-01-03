package com.starchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.TradeDetailPage;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.ICardHolderService;
import com.starchain.service.ICardService;
import com.starchain.util.HttpUtils;
import com.starchain.util.TpyshUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@RestController
@Slf4j
@Api(value = "pacificPay银行卡相关api", tags = {"pacificPay银行卡相关api"})
@RequestMapping("/card")
public class CardController {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private ICardHolderService cardHolderService;

    @Autowired
    private ICardService cardService;

    /**
     * 创建持卡人 同时创建卡
     */
    @ApiOperation(value = "创建持卡人并且创建卡")
    @PostMapping("/addCardAndHolder")
    public ClientResponse addCardHolder(@RequestBody CardHolderDto cardHolderDto) {
        if (ObjectUtils.isEmpty(cardHolderDto.getUserId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }

        if (ObjectUtils.isEmpty(cardHolderDto.getChannelId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        // 是否创建持卡人
        LambdaQueryWrapper<CardHolder> cardHolderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cardHolderLambdaQueryWrapper.eq(CardHolder::getUserId, cardHolderDto.getUserId());
        cardHolderLambdaQueryWrapper.eq(CardHolder::getChannelId, cardHolderDto.getChannelId());
        CardHolder cardHolder = cardHolderService.getOne(cardHolderLambdaQueryWrapper);
        if (cardHolder == null) {
            // 创建持卡人
            cardHolder = cardHolderService.addCardHolder(cardHolderDto);
            // 保存卡数据
            Card card = cardService.addCard(cardHolder);
            return ResultGenerator.genSuccessResult("申请创卡成功，正在审核中");
        }
        // 检查当前用户卡数量是否超过 4张
        Integer holderCardNum = cardService.checkCardNum(cardHolder.getId(), cardHolder.getChannelId(), cardHolder.getCardCode());
        if (holderCardNum >= 4) {
            return ResultGenerator.genFailResult("当前用户卡数量超过4张，无法创建新卡");
        }
        // 保存卡数据
        Card card = cardService.addCard(cardHolder);
        return ResultGenerator.genSuccessResult("申请创卡成功，正在审核中");
    }

    /**
     * 创建卡
     */
    @ApiOperation(value = "根据持卡人创建卡")
    @PostMapping("/addCardHolder")
    public void addCard() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 创建卡
        System.out.println("token=>" + token);
        Card card = new Card();
        card.setCardCode("TpyMDN6");
        card.setSaveOrderId("ORDER789012");
        card.setSaveAmount(new BigDecimal("10.00"));
        //  TPYSH持卡人ID
        card.setTpyshCardHolderId("20241224173541da328");

        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.addCard, token, JSONObject.toJSONString(card),
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        Card card1 = JSON.parseObject(str, Card.class);
        BeanUtils.copyProperties(card, card1);
    }


    /**
     * 查询商户余额
     */
    @ApiOperation(value = "查询商户余额")
    public void mchInfo() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 创建卡
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl()+ CardUrlConstants.mchInfo, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        JSONObject jsonObject = JSON.parseObject(str);
        // 查询商户余额：{"amount":523.70,"freeze":210.45}
        System.out.println("查询商户余额：" + jsonObject);
    }

    /*
     * 查询交易明细
     */
    @ApiOperation(value = "查询交易明细")
    public void tradeDetail() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TradeDetailPage tradeDetailPage = new TradeDetailPage();
        tradeDetailPage.setPageNum(1);
        tradeDetailPage.setPageSize(10);
        // 创建卡
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.tradeDetail, token, JSONObject.toJSONString(tradeDetailPage),
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        List<JSONObject> jsonObjects = JSON.parseArray(str, JSONObject.class);
        // 查询商户余额：{"amount":523.70,"freeze":210.45}
        System.out.println("查询交易明细：" + jsonObjects);
    }

}
