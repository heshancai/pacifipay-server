package com.starchain.controller;

import com.starchain.entity.CardHolder;
import com.starchain.entity.RemitCard;
import com.starchain.result.ClientResponse;
import com.starchain.result.ResultGenerator;
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
}
