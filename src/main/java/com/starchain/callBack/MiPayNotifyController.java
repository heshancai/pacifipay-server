package com.starchain.callBack;

import com.alibaba.fastjson2.JSON;
import com.starchain.config.PacificPayConfig;
import com.starchain.entity.RemitCardNotify;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.service.ICardService;
import com.starchain.service.IMiPayNotifyService;
import com.starchain.service.IRemitCardNotifyService;
import com.starchain.util.RSA2048Encrypt;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author
 * @date 2024-12-18
 * @Description 密付异步通知api
 */
@Slf4j
@RestController
@RequestMapping("/notify")
public class MiPayNotifyController {


    @Autowired
    private IRemitCardNotifyService remitCardNotifyService;

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private IMiPayNotifyService miPayNotifyService;

    @Autowired
    private ICardService cardService;

    @ApiOperation(value = "密付异步通知api")
    @PostMapping(value = "/miPayNotify")
    public String miPayNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(pacificPayConfig.getPrivateKey()));
        } catch (Exception e) {
            log.error("数据解密异常,{}", e.getMessage());
            return "fail";
        }
        MiPayCardNotifyResponse miPayNotifyResponse = JSON.parseObject(decrypt, MiPayCardNotifyResponse.class);
        checkRecharge(miPayNotifyResponse);
        RemitCardNotify remitCardNotify = remitCardNotifyService.checkDepositRecordIsExist(miPayNotifyResponse);


        // 检查数据信息
        switch (miPayNotifyResponse.getBusinessType()) {
            case "CardOpen": //卡开通通知
                miPayNotifyService.callBack(miPayNotifyResponse);
                break;
            case "CardRecharge": //卡充值通知
                break;
            case "CardWithdraw": // 卡提现通知
                break;
            case "Presave": //卡预存通知
                break;
            case "CardTrade": //卡流水通知
                break;
            case "CardCancel": //销卡通知
                break;
            case "VerifyCode": //卡验证码通知
                break;
            case "RemitCard": //申请汇款卡审核通知
                break;
            case "Remit": //申请汇款通知
                break;
            case "RemitCancel": //汇款撤销通知
                break;
            default:
                break;
        }

        // 修改数据状态
        log.info("pacificPayNotify:{}", miPayNotifyResponse);
        System.out.println(miPayNotifyResponse);
        return "success";
    }

    private void checkRecharge(MiPayCardNotifyResponse miPayNotifyResponse) {
        Assert.notNull(miPayNotifyResponse, "回调信息为空");
        Assert.hasText(miPayNotifyResponse.getNotifyId(), "通知ID不能为空");
        Assert.hasText(miPayNotifyResponse.getBusinessType(), "业务类型不能为空");
        Assert.hasText(miPayNotifyResponse.getCardId(), "卡ID不能为空");
        Assert.hasText(miPayNotifyResponse.getStatus(), "状态不能为空");
        Assert.hasText(miPayNotifyResponse.getStatusDesc(), "状态描述不能为空");
    }
}
