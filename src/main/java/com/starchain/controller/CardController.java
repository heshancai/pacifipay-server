package com.starchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.entity.Card;
import com.starchain.entity.CardRechargeRecord;
import com.starchain.entity.dto.CardDto;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.entity.dto.TradeDetailDto;
import com.starchain.entity.response.TradeDetailResponse;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.ICardRechargeRecordService;
import com.starchain.service.ICardService;
import com.starchain.util.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ICardRechargeRecordService cardRechargeRecordService;

    /**
     * 创建卡 每种类型限制一张
     */
    @ApiOperation(value = "根据持卡人创建卡")
    @PostMapping("/addCard")
    public ClientResponse addCard(@RequestBody CardHolderDto cardHolderDto) {
        // 检查当前用户卡数量是否超过 4张
        Integer holderCardNum = cardService.checkCardNum(cardHolderDto.getId(), cardHolderDto.getChannelId(), cardHolderDto.getCardCode(), cardHolderDto.getTpyshCardHolderId());
        if (holderCardNum >= 4) {
            return ResultGenerator.genFailResult("当前持卡人数量超过4张，无法创建新卡");
        }
        // 创建卡
        Card card = cardService.addCard(cardHolderDto);
        return ResultGenerator.genSuccessResult(card);
    }

    /*
     * 卡充值
     */
    @ApiOperation(value = "卡充值")
    @PostMapping("/applyRecharge")
    public ClientResponse applyRecharge(@RequestBody CardDto cardDto) {
        if (cardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getCardCode() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getUserId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getTpyshCardHolderId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getChannelId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getOrderAmount() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResultGenerator.genFailResult("输入的金额必须大于0");
        }
        //最新的卡充值未结束 无法进行新一轮充值
        LambdaQueryWrapper<CardRechargeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardRechargeRecord::getCardId, cardDto.getCardId());
        lambdaQueryWrapper.eq(CardRechargeRecord::getUserId, cardDto.getUserId());
        lambdaQueryWrapper.eq(CardRechargeRecord::getCardCode, cardDto.getCardCode());
        lambdaQueryWrapper.orderByDesc(CardRechargeRecord::getId).last("LIMIT 1");
        CardRechargeRecord cardRechargeRecord = cardRechargeRecordService.getOne(lambdaQueryWrapper);
        if (cardRechargeRecord != null && cardRechargeRecord.getStatus() == 0) {
            return ResultGenerator.genFailResult("上一笔充值正在处理中，无法进行新一轮充值");
        }
        try {
            // 进行卡充值
            CardRechargeRecord rechargeRecord = cardService.applyRecharge(cardDto);
            return ResultGenerator.genSuccessResult(rechargeRecord);
        } catch (Exception e) {
            log.error("查询卡失败", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
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
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.MCH_INFO, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
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
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.TRADE_DETAIL, token, JSONObject.toJSONString(tradeDetailDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            System.out.println("查询交易明细：" + str);
            List<TradeDetailResponse> tradeDetailResponseList = JSON.parseArray(str, TradeDetailResponse.class);
            List<TradeDetailResponse> sortedList = tradeDetailResponseList.stream().sorted(Comparator.comparing(TradeDetailResponse::getBalance)).collect(Collectors.toList());
            return ResultGenerator.genSuccessResult(sortedList);
        } catch (Exception e) {
            log.error("查询商户交易明细失败", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }

    }


    /*
     * 查询卡
     */
    @ApiOperation(value = "查询卡")
    @PostMapping("/getCardDetail")
    public ClientResponse getCardDetail(@RequestBody CardDto cardDto) {
        if (cardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getCardCode() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.GET_CARD_DETAIL, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            Card card = JSON.parseObject(str, Card.class);
            return ResultGenerator.genSuccessResult(card);
        } catch (Exception e) {
            log.error("查询卡失败", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    /*
     * 申请销卡
     */
    @ApiOperation(value = "申请销卡")
    @PostMapping("/deleteCard")
    public ClientResponse deleteCard(@RequestBody CardDto cardDto) {
        if (cardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getCardCode() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getCardId, cardDto.getCardId());
        queryWrapper.eq(Card::getCardCode, cardDto.getCardCode());
        Card card = cardService.getOne(queryWrapper);
        if (card == null) {
            return ResultGenerator.genFailResult("卡信息不存在");
        }
        try {
            Boolean result = cardService.deleteCard(cardDto);
            return ResultGenerator.genSuccessResult("申请销卡正在处理中");
        } catch (Exception e) {
            log.error("申请销卡失败", e);
            return ResultGenerator.genFailResult("申请销卡失败");
        }
    }

    /*
     * 作废 现无法使用-申请换卡
     */
//    @ApiOperation(value = "申请换卡")
//    @PostMapping("/changeCard")
//    public ClientResponse changeCard(@RequestBody CardDto cardDto) {
//        if (cardDto.getCardId() == null) {
//            return ResultGenerator.genFailResult("dto 不能为null");
//        }
//        if (cardDto.getCardCode() == null) {
//            return ResultGenerator.genFailResult("dto 不能为null");
//        }
//        if (cardDto.getTpyshCardHolderId() == null) {
//            return ResultGenerator.genFailResult("dto 不能为null");
//        }
//
//        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Card::getCardId, cardDto.getCardId());
//        queryWrapper.eq(Card::getCardCode, cardDto.getCardCode());
//        queryWrapper.eq(Card::getStatus, 1);
//        queryWrapper.eq(Card::getStatus, 1);
//        queryWrapper.eq(Card::getTpyshCardHolderId, cardDto.getTpyshCardHolderId());
//        Card card = cardService.getOne(queryWrapper);
//        if (card == null) {
//            return ResultGenerator.genFailResult("原卡信息不存在");
//        }
//        try {
//            Card card1 = cardService.changeCard(card);
//            return ResultGenerator.genSuccessResult(card1);
//        } catch (Exception e) {
//            log.error("申请销卡失败", e);
//            return ResultGenerator.genFailResult("申请销卡失败");
//        }
//    }

    @ApiOperation(value = "修改卡限额")
    @PostMapping("/updateLimit")
    public ClientResponse updateLimit(@RequestBody CardDto cardDto) {
        if (cardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getCardCode() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getCardId, cardDto.getCardId());
        queryWrapper.eq(Card::getCardCode, cardDto.getCardCode());
        Card card = cardService.getOne(queryWrapper);
        if (card == null) {
            return ResultGenerator.genFailResult("卡信息不存在");
        }
        try {
            Boolean result = cardService.deleteCard(cardDto);
            return ResultGenerator.genSuccessResult("申请销卡正在处理中");
        } catch (Exception e) {
            log.error("申请销卡失败", e);
            return ResultGenerator.genFailResult("申请销卡失败");
        }
    }
}
