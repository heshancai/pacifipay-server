package com.starchain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.common.entity.RemitApplicationRecord;
import com.starchain.common.entity.RemitCard;
import com.starchain.common.entity.dto.RemitApplicationRecordDto;
import com.starchain.common.entity.dto.RemitCardDto;
import com.starchain.common.entity.dto.RemitRateDto;
import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.common.enums.MoneyKindEnum;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
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
        if (remitCardDto.getUserId() == null) {
            return ResultGenerator.genFailResult("userId不能为空");
        }
        if (remitCardDto.getBusinessId() == null) {
            return ResultGenerator.genFailResult("channelId不能为空");
        }
        if (remitCardDto.getRemitBankNo() == null) {
            return ResultGenerator.genFailResult("银行卡号");
        }
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
        checkQueryWrapper.eq(RemitCard::getCancelStatus, 0);
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
        try {
            LambdaQueryWrapper<RemitCard> checkQueryWrapper = new LambdaQueryWrapper<>();
            checkQueryWrapper.eq(RemitCard::getTpyCardId, remitCardDto.getCardId());
            checkQueryWrapper.eq(RemitCard::getCancelStatus, 0);
            RemitCard remitCard = remitCardService.getOne(checkQueryWrapper);
            Assert.notNull(remitCard, "汇款卡不存在或已删除");
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
        if (remitCardDto.getRemitCode() == null) {
            return ResultGenerator.genFailResult("remitCode不能为空");
        }
        if (remitCardDto.getUserId() == null) {
            return ResultGenerator.genFailResult("userId不能为空");
        }
        if (remitCardDto.getBusinessId() == null) {
            return ResultGenerator.genFailResult("channelId不能为空");
        }
        try {
            Boolean result = remitCardService.delRemitCard(remitCardDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    /**
     * 查询汇款单详情
     */
    @ApiOperation(value = "查询汇款单详情")
    @PostMapping("/remitDetail")
    public ClientResponse remitDetail(@RequestBody RemitCardDto remitCardDto) {

        if (remitCardDto.getOrderId() == null) {
            return ResultGenerator.genFailResult("orderId不能为空");
        }
        if (remitCardDto.getRemitCode() == null) {
            return ResultGenerator.genFailResult("remitCode不能为空");
        }
        try {
            JSONObject b = remitCardService.remitDetail(remitCardDto);
            return ResultGenerator.genSuccessResult(b);
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
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getBusinessId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getRemitCode())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitApplicationRecordDto.getToMoneyKind())
                && (MoneyKindEnum.CNY.getMoneyKindCode().equals(remitApplicationRecordDto.getToMoneyKind())
                || MoneyKindEnum.USD.getMoneyKindCode().equals(remitApplicationRecordDto.getToMoneyKind()))) {
            return ResultGenerator.genFailResult("dto不能为空 或者不支持的币种");
        }
        if (ObjectUtils.isEmpty(remitApplicationRecordDto.getToAmount())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (remitApplicationRecordDto.getExtraParams() == null) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitApplicationRecordDto.getExtraParams().getString("remitBankNo"))) {
            return ResultGenerator.genFailResult("银行卡号不能为空");
        }

        if (remitApplicationRecordDto.getExtraParams() != null) {
            validateRemitApplicationRecord(remitApplicationRecordDto);
        }
        // 交易上一笔交易是否完成
        LambdaQueryWrapper<RemitApplicationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitApplicationRecord::getUserId, remitApplicationRecordDto.getUserId());
        queryWrapper.eq(RemitApplicationRecord::getBusinessId, remitApplicationRecordDto.getBusinessId());
        queryWrapper.eq(RemitApplicationRecord::getRemitCode, remitApplicationRecordDto.getRemitCode());
        queryWrapper.eq(RemitApplicationRecord::getToMoneyKind, remitApplicationRecordDto.getToMoneyKind());
        queryWrapper.orderByDesc(RemitApplicationRecord::getId).last("LIMIT 1");
        RemitApplicationRecord remitApplicationRecord = remitApplicationRecordService.getOne(queryWrapper);
        if (remitApplicationRecord != null && remitApplicationRecord.getStatus() == 0) {
            return ResultGenerator.genFailResult("上一笔汇款正在处理中，无法进行新一轮汇款");
        }
        try {
            // 校验用户钱包余额是否足够
            userWalletBalanceService.checkUserBalance(remitApplicationRecordDto.getUserId(), remitApplicationRecordDto.getBusinessId(), remitApplicationRecordDto.getToAmount(), MiPayNotifyType.Remit.getType());
            Boolean result = remitApplicationRecordService.applyRemit(remitApplicationRecordDto);
            return ResultGenerator.genSuccessResult(result);
        } catch (Exception e) {
            log.error("服务异常", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
    }

    public ClientResponse validateRemitApplicationRecord(RemitApplicationRecordDto remitApplicationRecordDto) {

        String remitTypeCode = remitApplicationRecordDto.getRemitCode();

        switch (remitTypeCode) {
            case "ELR":
            case "VNPR_IND":
                if (!StringUtils.hasText(remitApplicationRecordDto.getExtraParams().getString("remitName"))) {
                    return ResultGenerator.genFailResult("姓名不能为空");
                }

                if ("VNPR_IND".equals(remitTypeCode)) {
                    if (!StringUtils.hasText(remitApplicationRecordDto.getMobileNumber())) {
                        return ResultGenerator.genFailResult("手机号不能为空");
                    }
                    if (!StringUtils.hasText(remitApplicationRecordDto.getEmail())) {
                        return ResultGenerator.genFailResult("邮箱不能为空");
                    }
                    if (!StringUtils.hasText(remitApplicationRecordDto.getBankBranchCode())) {
                        return ResultGenerator.genFailResult("支行编码不能为空");
                    }
                }
                break;

            case "LNR_IND":
                if (!StringUtils.hasText(remitApplicationRecordDto.getRemitLastName())) {
                    return ResultGenerator.genFailResult("姓不能为空");
                }
                if (!StringUtils.hasText(remitApplicationRecordDto.getRemitFirstName())) {
                    return ResultGenerator.genFailResult("名不能为空");
                }
                if (!StringUtils.hasText(remitApplicationRecordDto.getRemitBankNo())) {
                    return ResultGenerator.genFailResult("银行卡号不能为空");
                }
                if (!StringUtils.hasText(remitApplicationRecordDto.getToMoneyCountry3())) {
                    return ResultGenerator.genFailResult("汇款国家编码不能为空");
                }
                if (!StringUtils.hasText(remitApplicationRecordDto.getBankCode())) {
                    return ResultGenerator.genFailResult("银行编码不能为空");
                }
                if (!StringUtils.hasText(remitApplicationRecordDto.getBankBranchCode())) {
                    return ResultGenerator.genFailResult("支行编码不能为空");
                }
                break;

            case "UQR":
                if (!StringUtils.hasText(remitApplicationRecordDto.getRemitTpyCardId())) {
                    return ResultGenerator.genFailResult("Tpy汇款卡ID不能为空");
                }
                break;

            default:
                return ResultGenerator.genFailResult("不支持的汇款类型编码");
        }

        return ResultGenerator.genSuccessResult(); // 所有校验通过，返回成功
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
