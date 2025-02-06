package com.starchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.starchain.common.config.PacificPayConfig;
import com.starchain.common.constants.CardUrlConstants;
import com.starchain.common.entity.Card;
import com.starchain.common.entity.CardHolder;
import com.starchain.common.entity.CardRechargeRecord;
import com.starchain.common.entity.MerchantWallet;
import com.starchain.common.entity.dto.CardDto;
import com.starchain.common.entity.dto.TradeDetailDto;
import com.starchain.common.entity.response.TradeDetailResponse;
import com.starchain.common.enums.CardStatusEnum;
import com.starchain.common.enums.CreateStatusEnum;
import com.starchain.common.enums.MoneyKindEnum;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.common.util.HttpUtils;
import com.starchain.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Autowired
    private ICardHolderService cardHolderService;

    @Autowired
    private IRemitApplicationRecordService remitApplicationRecordService;

    @Autowired
    private IMerchantWalletService merchantWalletService;

    @Value("${app.id}")
    private String appId;


    /**
     * 创建卡 每种类型限制4张
     */
    @ApiOperation(value = "根据持卡人创建卡")
    @PostMapping("/addCard")
    public ClientResponse addCard(@RequestBody CardDto cardDto) {
        // 检查当前用户卡数量是否超过 4张
        Integer holderCardNum = cardService.checkCardNum(cardDto.getBusinessId(), cardDto.getCardCode(), cardDto.getTpyshCardHolderId());
        if (holderCardNum >= 4) {
            return ResultGenerator.genFailResult("当前持卡人数量超过4张，无法创建新卡");
        }
        // 创建卡
        Card card = cardService.addCard(cardDto);
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
        if (cardDto.getBusinessId() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getOrderAmount() == null) {
            return ResultGenerator.genFailResult("dto 不能为null");
        }
        if (cardDto.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResultGenerator.genFailResult("输入的金额必须大于0");
        }

        // 持卡人合法性校验
        CardHolder cardHolder = validateCardHolder(cardDto);
        if (cardHolder == null) {
            return ResultGenerator.genFailResult("持卡人不存在");
        }

        // 卡合法性校验
        Card card = validateCard(cardDto);
        if (card == null) {
            return ResultGenerator.genFailResult("卡状态异常 无法充值");
        }

        // 检查充值状态
        if (cardService.isRechargeInProgress(cardDto)) {
            return ResultGenerator.genFailResult("最新的卡充值未结束 无法进行新一轮充值");
        }

        // 检查汇款状态
        if (remitApplicationRecordService.isRemitInProgress(cardDto.getUserId(), cardDto.getBusinessId())) {
            return ResultGenerator.genFailResult("最新的汇款未结束 无法进行新一轮充值");
        }

        try {
            // 进行卡充值
            CardRechargeRecord rechargeRecord = cardService.applyRecharge(cardDto);
            return ResultGenerator.genSuccessResult(rechargeRecord);
        } catch (Exception e) {
            log.error("查询卡失败, cardDto: {}", cardDto, e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    // 持卡人校验逻辑封装
    private CardHolder validateCardHolder(CardDto cardDto) {
        LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardHolder::getUserId, cardDto.getUserId())
                .eq(CardHolder::getCardCode, cardDto.getCardCode())
                .eq(CardHolder::getTpyshCardHolderId, cardDto.getTpyshCardHolderId())
                .eq(CardHolder::getBusinessId, cardDto.getBusinessId())
                .eq(CardHolder::getStatus, CreateStatusEnum.SUCCESS.getCode());
        return cardHolderService.getOne(lambdaQueryWrapper);
    }

    // 卡校验逻辑封装
    private Card validateCard(CardDto cardDto) {
        LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cardLambdaQueryWrapper.eq(Card::getCardId, cardDto.getCardId())
                .eq(Card::getTpyshCardHolderId, cardDto.getTpyshCardHolderId())
                .eq(Card::getCreateStatus, CreateStatusEnum.SUCCESS.getCode())
                .eq(Card::getCardStatus, CardStatusEnum.NORMAL.getCardStatus());
        return cardService.getOne(cardLambdaQueryWrapper);
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
        LambdaUpdateWrapper<MerchantWallet> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MerchantWallet::getMerchantId, appId);
        MerchantWallet merchantWallet = merchantWalletService.getOne(updateWrapper);
        if (merchantWallet == null) {
            merchantWallet = new MerchantWallet();
            merchantWallet.setAmount((BigDecimal) jsonObject.get("amount"));
            merchantWallet.setMerchantId(appId);
            merchantWallet.setAppId(appId);
            merchantWallet.setFreeze((BigDecimal) jsonObject.get("freeze"));
            merchantWallet.setCreateTime(LocalDateTime.now());
            merchantWallet.setUpdateTime(LocalDateTime.now());
            merchantWallet.setMoneyKind(MoneyKindEnum.USD.getMoneyKindCode());
            merchantWalletService.save(merchantWallet);
        } else {
            merchantWallet.setAmount((BigDecimal) jsonObject.get("amount"));
            merchantWallet.setFreeze((BigDecimal) jsonObject.get("freeze"));
            merchantWallet.setUpdateTime(LocalDateTime.now());
            merchantWalletService.updateById(merchantWallet);
        }
        return ResultGenerator.genSuccessResult(merchantWallet);
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
            Card returnCard = JSON.parseObject(str, Card.class);
            LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Card::getCardId, returnCard.getCardId());
            updateWrapper.eq(Card::getCardCode, returnCard.getCardCode());
            updateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
            updateWrapper.set(Card::getCardNo, returnCard.getCardNo());
            updateWrapper.set(Card::getCardStatus, returnCard.getCardStatus());
            updateWrapper.set(Card::getCreateTime, returnCard.getCreateTime());
            updateWrapper.set(Card::getCardExpDate, returnCard.getCardExpDate());
            updateWrapper.set(Card::getBankType, returnCard.getBankType());
            updateWrapper.set(Card::getKycType, returnCard.getKycType());
            updateWrapper.set(Card::getCardAmount, returnCard.getCardAmount());
            updateWrapper.set(Card::getPhysicsType, returnCard.getPhysicsType());
            updateWrapper.set(Card::getSingleLimit, returnCard.getSingleLimit());
            updateWrapper.set(Card::getLocalUpdateTime, LocalDateTime.now());
            cardService.update(updateWrapper);
            return ResultGenerator.genSuccessResult(returnCard);
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
            Boolean result = cardService.updateLimit(cardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("修改卡限额失败", e);
            return ResultGenerator.genFailResult("申请销卡失败");
        }
    }

    @ApiOperation(value = "锁定卡")
    @PostMapping("/lockCard")
    public ClientResponse lockCard(@RequestBody CardDto cardDto) {
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
        if (card != null && !card.getCardStatus().equals(CardStatusEnum.NORMAL.getCardStatus())) {
            return ResultGenerator.genFailResult("卡未在使用中 无须锁定");
        }
        try {
            Boolean result = cardService.lockCard(card);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("申请销卡失败", e);
            return ResultGenerator.genFailResult("服务异常，申请销卡失败");
        }
    }

    @ApiOperation(value = "解锁卡")
    @PostMapping("/unlockCard")
    public ClientResponse unlockCard(@RequestBody CardDto cardDto) {
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
        if (card != null && !card.getCardStatus().equals(CardStatusEnum.FREEZING.getCardStatus())) {
            return ResultGenerator.genFailResult("开未锁定 无须解锁");
        }
        try {
            Boolean result = cardService.unlockCard(card);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("申请销卡失败", e);
            return ResultGenerator.genFailResult("服务异常，解锁卡失败");
        }
    }
}
