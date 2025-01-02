package com.starchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.TradeDetailPage;
import com.starchain.enums.CardCodeEnum;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.CardHolderService;
import com.starchain.util.TpyshUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@RestController("/card")
@Slf4j
public class CardController {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private CardHolderService cardHolderService;

    /**
     * 创建持卡人
     */
    @ApiOperation(value = "创建持卡人并且创建卡")
    @PostMapping("/addCardHolder")
    public ClientResponse addCardHolder(@RequestBody CardHolder cardHolder) {
        Boolean result = cardHolderService.addCardHolder(cardHolder);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 创建卡
     */
    @ApiOperation(value = "根据持卡人创建卡")
    @PostMapping("/addCard")
    public void addCard() {
        String token = null;
        try {
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET);
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

        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.addCard, token, JSONObject.toJSONString(card),
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        Card card1 = JSON.parseObject(str, Card.class);
        BeanUtils.copyProperties(card, card1);
    }


    /**
     * 查询商户余额
     */
    public void mchInfo() {
        String token = null;
        try {
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 创建卡
        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.mchInfo, token, "",
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        JSONObject jsonObject = JSON.parseObject(str);
        // 查询商户余额：{"amount":523.70,"freeze":210.45}
        System.out.println("查询商户余额：" + jsonObject);
    }

    /*
     * 查询交易明细
     */
    public void tradeDetail() {
        String token = null;
        try {
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        TradeDetailPage tradeDetailPage = new TradeDetailPage();
        tradeDetailPage.setPageNum(1);
        tradeDetailPage.setPageSize(10);
        // 创建卡
        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.tradeDetail, token, JSONObject.toJSONString(tradeDetailPage),
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        List<JSONObject> jsonObjects = JSON.parseArray(str, JSONObject.class);
        // 查询商户余额：{"amount":523.70,"freeze":210.45}
        System.out.println("查询交易明细：" + jsonObjects);
    }

}
