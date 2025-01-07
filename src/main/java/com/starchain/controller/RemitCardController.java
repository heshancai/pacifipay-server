package com.starchain.controller;

import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.RemitApplicationRecordDto;
import com.starchain.entity.dto.RemitRateDto;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.IRemitCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RemitCardController {


    @Autowired
    private IRemitCardService remitCardService;

    @Autowired
    private IRemitApplicationRecordService remitApplicationRecord;

    /**
     * 添加收款卡
     */
    @ApiOperation(value = "添加收款卡信息")
    @PostMapping("/addRemitCard")
    public ClientResponse addRemitCard(@RequestBody RemitCard remitCard) {
        if (ObjectUtils.isEmpty(remitCard.getUserId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (ObjectUtils.isEmpty(remitCard.getChannelId())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitCard.getRemitFirstName())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitCard.getRemitLastName())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        if (!StringUtils.hasText(remitCard.getRemitBankNo())) {
            return ResultGenerator.genFailResult("dto不能为空");
        }

        Boolean result = remitCardService.addRemitCard(remitCard);
        return ResultGenerator.genSuccessResult(result);
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
//        if (!StringUtils.hasText(remitApplicationRecordDto.getRemitFirstName())) {
//            return ResultGenerator.genFailResult("dto不能为空");
//        }
//        if (!StringUtils.hasText(remitApplicationRecordDto.getRemitLastName())) {
//            return ResultGenerator.genFailResult("dto不能为空");
//        }
//        if (!StringUtils.hasText(remitApplicationRecordDto.getRemitBankNo())) {
//            return ResultGenerator.genFailResult("dto不能为空");
//        }

        Boolean result = remitApplicationRecord.applyRemit(remitApplicationRecordDto);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 申请汇款
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
