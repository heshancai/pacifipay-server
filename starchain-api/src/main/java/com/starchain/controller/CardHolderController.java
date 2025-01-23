package com.starchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.common.entity.CardHolder;
import com.starchain.common.entity.dto.CardHolderDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.service.ICardHolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
@RestController
@Slf4j
@Api(value = "miPay 持卡人相关api", tags = {"miPay 持卡人相关api相关api"})
@RequestMapping("/cardHolder")
public class CardHolderController {
    @Autowired
    private ICardHolderService cardHolderService;

    /**
     * 创建持卡人 免费创建
     */
    @ApiOperation(value = "创建持卡人")
    @PostMapping("/add")
    public ClientResponse addCardHolder(@RequestBody CardHolderDto cardHolderDto) {
        if (ObjectUtils.isEmpty(cardHolderDto.getUserId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getChannelId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        // 创建持卡人
        CardHolder cardHolder = cardHolderService.addCardHolder(cardHolderDto);
        return ResultGenerator.genSuccessResult(cardHolder);
    }

    /**
     * 修改持卡人
     */
    @ApiOperation(value = "修改持卡人")
    @PostMapping("/update")
    public ClientResponse updateCardHolder(@RequestBody CardHolderDto cardHolderDto) {
        if (ObjectUtils.isEmpty(cardHolderDto.getUserId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getChannelId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getCardCode())) {
            return ResultGenerator.genFailResult("卡类型编码不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getTpyshCardHolderId())) {
            return ResultGenerator.genFailResult("太平洋持卡人ID不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getMerchantCardHolderId())) {
            return ResultGenerator.genFailResult("持卡人ID不能为空");
        }
        LambdaQueryWrapper<CardHolder> cardHolderQueryWrapper = new LambdaQueryWrapper<>();
        cardHolderQueryWrapper.eq(CardHolder::getUserId, cardHolderDto.getUserId());
        cardHolderQueryWrapper.eq(CardHolder::getId, cardHolderDto.getId());
        cardHolderQueryWrapper.eq(CardHolder::getChannelId, cardHolderDto.getChannelId());
        cardHolderQueryWrapper.eq(CardHolder::getCardCode, cardHolderDto.getCardCode());
        cardHolderQueryWrapper.eq(CardHolder::getMerchantCardHolderId, cardHolderDto.getMerchantCardHolderId());
        cardHolderQueryWrapper.eq(CardHolder::getTpyshCardHolderId, cardHolderDto.getTpyshCardHolderId());
        CardHolder cardHolder = cardHolderService.getOne(cardHolderQueryWrapper);
        if (cardHolder == null) {
            return ResultGenerator.genFailResult("持卡人不存在,请创建持卡人");
        }
        CardHolder result = cardHolderService.updateCardHolder(cardHolderDto);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 查询持卡人
     */
    @ApiOperation(value = "查询持卡人")
    @PostMapping("/getCardHolder")
    public ClientResponse getCardHolder(@RequestBody CardHolderDto cardHolderDto) {
        if (ObjectUtils.isEmpty(cardHolderDto.getCardCode())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(cardHolderDto.getTpyshCardHolderId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        LambdaQueryWrapper<CardHolder> cardHolderLambdaQueryWrapper=new LambdaQueryWrapper<>();
        cardHolderLambdaQueryWrapper.eq(CardHolder::getMerchantCardHolderId,cardHolderDto.getMerchantCardHolderId());
        CardHolder result = cardHolderService.getOne(cardHolderLambdaQueryWrapper);
        if (result == null) {
            return ResultGenerator.genFailResult("持卡人不存在");
        }
        CardHolder cardHolder = cardHolderService.getCardHolder(cardHolderDto);
        return ResultGenerator.genSuccessResult(cardHolder);
    }

}
