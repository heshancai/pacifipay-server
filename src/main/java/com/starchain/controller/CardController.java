package com.starchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.TradeDetailDto;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.entity.response.TradeDetailResponse;
import com.starchain.enums.OrderTypeEnum;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.ICardHolderService;
import com.starchain.service.ICardService;
import com.starchain.util.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ICardService cardService;



    /**
     * 创建卡 每种类型限制一张
     */
    @ApiOperation(value = "根据持卡人创建卡")
    @PostMapping("/addCard")
    public ClientResponse addCard(@RequestBody CardHolderDto cardHolderDto) {
        // 检查当前用户卡数量是否超过 4张
        Integer holderCardNum = cardService.checkCardNum(cardHolderDto.getId(), cardHolderDto.getChannelId(), cardHolderDto.getCardCode());
        if (holderCardNum >= 4) {
            return ResultGenerator.genFailResult("当前用户卡数量超过4张，无法创建新卡");
        }
        // 创建卡
        Card card = cardService.addCard(cardHolderDto);
        return ResultGenerator.genSuccessResult(card);
    }


    /**
     * 查询商户余额
     */
    @ApiOperation(value = "查询商户余额")
    @PostMapping("/mchInfo")
    public ClientResponse mchInfo() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.mchInfo, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        JSONObject jsonObject = JSON.parseObject(str);
        return ResultGenerator.genSuccessResult(jsonObject);
    }

    /*
     * 查询商户交易明细 支持查询多种类型数据
     */
    @ApiOperation(value = "查询商户交易明细")
    @PostMapping("/tradeDetail")
    public ClientResponse tradeDetail(@RequestBody TradeDetailDto tradeDetailDto) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.tradeDetail, token, JSONObject.toJSONString(tradeDetailDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        List<TradeDetailResponse> tradeDetailResponseList = JSON.parseArray(str, TradeDetailResponse.class);
        System.out.println("查询交易明细：" + tradeDetailResponseList);
        return ResultGenerator.genSuccessResult(tradeDetailResponseList);
    }

}
