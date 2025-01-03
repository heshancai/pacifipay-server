package com.starchain.callBack;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.entity.dto.MiPayNotifyDto;
import com.starchain.util.RSA2048Encrypt;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PacificPayConfig pacificPayConfig;

    @ApiOperation(value = "卡开通通知")
    @PostMapping(value = "/miPayNotify")
    public String cardOpenNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(pacificPayConfig.getPrivateKey()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MiPayNotifyDto miPayNotifyDto = JSON.parseObject(decrypt, MiPayNotifyDto.class);
        switch (miPayNotifyDto.getBusinessType()) {
            case "CardOpen": //卡开通通知
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
            default:
                break;
        }
        // 检查回调状态
        // 检查数据信息
        // 修改数据状态
        log.info("pacificPayNotify:{}", miPayNotifyDto);
        System.out.println(miPayNotifyDto);
        return "success";
    }


}
