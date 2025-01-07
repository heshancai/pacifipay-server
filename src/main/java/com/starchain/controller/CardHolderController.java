package com.starchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
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
        // 每个用户只能创建一个持卡人
        LambdaQueryWrapper<CardHolder> cardHolderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cardHolderLambdaQueryWrapper.eq(CardHolder::getUserId, cardHolderDto.getUserId());
        cardHolderLambdaQueryWrapper.eq(CardHolder::getChannelId, cardHolderDto.getChannelId());
        CardHolder cardHolder = cardHolderService.getOne(cardHolderLambdaQueryWrapper);
        if (cardHolder == null) {
            // 创建持卡人
            cardHolder = cardHolderService.addCardHolder(cardHolderDto);
        }
        return ResultGenerator.genSuccessResult(cardHolder);
    }
}
