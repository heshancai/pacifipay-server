package com.starchain.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.entity.RemitApplicationRecord;
import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.RemitApplicationRecordDto;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.entity.dto.RemitRateDto;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.IRemitCardService;
import com.starchain.service.IUserWalletBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @date 2025-01-02
 * @Description 汇款Controller
 */
@Api(value = "PacificPay汇款相关api", tags = {"PacificPay汇款相关api"})
@RestController
@RequestMapping("/remit")
@Slf4j
public class RemitCardController {


    @Autowired
    private IRemitCardService remitCardService;

    @Autowired
    private IRemitApplicationRecordService remitApplicationRecordService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;

    /**
     * 添加收款卡
     */
    @ApiOperation(value = "添加收款卡信息")
    @PostMapping("/addRemitCard")
    public ClientResponse addRemitCard(@RequestBody RemitCardDto remitCardDto) {

        try {
            Boolean result = remitCardService.addRemitCard(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult("服务异常");
        }
    }

    /**
     * 获取收款卡信息
     */
    @ApiOperation(value = "获取收款卡信息")
    @PostMapping("/getRemitCard")
    public ClientResponse getRemitCard(@RequestBody RemitCardDto remitCardDto) {
        if (remitCardDto.getTpyCardId() == null) {
            return ResultGenerator.genFailResult("TpyCardId不能为空");
        }
        if (remitCardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("cardId不能为空");
        }
        if (remitCardDto.getRemitCode() == null) {
            return ResultGenerator.genFailResult("remitCode不能为空");
        }
        LambdaQueryWrapper<RemitCard> checkQueryWrapper = new LambdaQueryWrapper<>();
        checkQueryWrapper.eq(RemitCard::getTpyCardId, remitCardDto.getCardId());
        checkQueryWrapper.eq(RemitCard::getCardStatus, 0);
        RemitCard remitCard = remitCardService.getOne(checkQueryWrapper);
        Assert.notNull(remitCard, "汇款卡不存在或已删除");
        try {
            RemitCard result = remitCardService.getRemitCard(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult("服务异常");
        }
    }


    /**
     * 修改收款卡信息
     */
    @ApiOperation(value = "修改收款卡信息")
    @PostMapping("/updateRemitCard")
    public ClientResponse updateRemitCard(@RequestBody RemitCardDto remitCardDto) {
        if (remitCardDto.getTpyCardId() == null) {
            return ResultGenerator.genFailResult("TpyCardId不能为空");
        }
        LambdaQueryWrapper<RemitCard> checkQueryWrapper = new LambdaQueryWrapper<>();
        checkQueryWrapper.eq(RemitCard::getTpyCardId, remitCardDto.getCardId());
        checkQueryWrapper.eq(RemitCard::getCardStatus, 0);
        RemitCard remitCard = remitCardService.getOne(checkQueryWrapper);
        Assert.notNull(remitCard, "汇款卡不存在或已删除");
        try {
            Boolean result = remitCardService.updateRemitCard(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult("服务异常");
        }
    }


    /**
     * 添加收款卡
     */
    @ApiOperation(value = "删除收款卡信息")
    @PostMapping("/delRemitCard")
    public ClientResponse delRemitCard(@RequestBody RemitCardDto remitCardDto) {

        if (remitCardDto.getTpyCardId() == null) {
            return ResultGenerator.genFailResult("TpyCardId不能为空");
        }
        if (remitCardDto.getCardId() == null) {
            return ResultGenerator.genFailResult("cardId不能为空");
        }
        try {
            Boolean result = remitCardService.delRemitCard(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult("服务异常");
        }
    }

    /**
     * 汇款单详情
     */
    @ApiOperation(value = "汇款单详情")
    @PostMapping("/remitDetail")
    public ClientResponse remitDetail(@RequestBody RemitCardDto remitCardDto) {

        if (remitCardDto.getOrderId() == null) {
            return ResultGenerator.genFailResult("orderId不能为空");
        }
        if (remitCardDto.getRemitCode() == null) {
            return ResultGenerator.genFailResult("remitCode不能为空");
        }
        try {
            Boolean result = remitCardService.remitDetail(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult("服务异常");
        }
    }

    /**
     * 申请汇款
     */
    @ApiOperation(value = "申请汇款")
    @PostMapping("/applyRemit")
    public ClientResponse applyRemit(@RequestBody RemitApplicationRecordDto remitApplicationRecordDto) {
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getUserId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getChannelId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getRemitCode())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitApplicationRecordDto.getToMoneyKind())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getToAmount())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        // 交易上一笔交易是否完成
        LambdaQueryWrapper<RemitApplicationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitApplicationRecord::getUserId, remitApplicationRecordDto.getUserId());
        queryWrapper.eq(RemitApplicationRecord::getChannelId, remitApplicationRecordDto.getChannelId());
        queryWrapper.eq(RemitApplicationRecord::getRemitCode, remitApplicationRecordDto.getRemitCode());
        queryWrapper.eq(RemitApplicationRecord::getToMoneyKind, remitApplicationRecordDto.getToMoneyKind());
        queryWrapper.eq(RemitApplicationRecord::getToAmount, remitApplicationRecordDto.getToAmount());
        queryWrapper.orderByDesc(RemitApplicationRecord::getId).last("LIMIT 1");
        RemitApplicationRecord remitApplicationRecord = remitApplicationRecordService.getOne(queryWrapper);
        if (remitApplicationRecord != null && remitApplicationRecord.getStatus() == 0) {
            return ResultGenerator.genFailResult("上一笔汇款正在处理中，无法进行新一轮汇款");
        }
        try {
            // 校验用户钱包余额是否足够
            userWalletBalanceService.checkUserBalance(remitApplicationRecordDto.getUserId(), remitApplicationRecordDto.getChannelId(), remitApplicationRecordDto.getToAmount());
            Boolean result = remitApplicationRecordService.applyRemit(remitApplicationRecordDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    /**
     * 获取汇款汇率
     */
    @ApiOperation(value = "获取汇款汇率")
    @PostMapping("/getRemitRate")
    public ClientResponse getRemitRate(@RequestBody RemitRateDto remitRateDto) {
        if (ObjectUtils.isEmpty(remitRateDto.getRemitCode())) {
            return ResultGenerator.genFailResult("汇款类型编码不能为空");
        }
        if (ObjectUtils.isEmpty(remitRateDto.getToMoneyKind())) {
            return ResultGenerator.genFailResult("汇款目标币种不能为空");
        }

        RemitRateDto result = remitCardService.getRemitRate(null, remitRateDto);
        return ResultGenerator.genSuccessResult(result);
    }

}
