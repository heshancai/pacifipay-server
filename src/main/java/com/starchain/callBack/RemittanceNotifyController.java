package com.starchain.callBack;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.constants.CardUrlConstants;
import com.starchain.util.RSA2048Encrypt;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @date 2025-01-02
 * @Description 汇款api异步通知
 */
@Slf4j
@RestController
public class RemittanceNotifyController {

    @ApiOperation(value = "申请汇款通知")
    @PostMapping(value = "/cardRemitNotify")
    public String cardRemitNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(CardUrlConstants.PRIVATEKEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject res = JSON.parseObject(decrypt);
        // 检查回调状态
        // 检查数据信息
        // 修改数据状态
        log.info("pacificPayNotify:{}", res);
        System.out.println(res);
        return "success";
    }

    @ApiOperation(value = "汇款撤销通知")
    @PostMapping(value = "/remitCancelNotify")
    public String remitCancelNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(CardUrlConstants.PRIVATEKEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject res = JSON.parseObject(decrypt);
        // 检查回调状态
        // 检查数据信息
        // 修改数据状态
        log.info("pacificPayNotify:{}", res);
        System.out.println(res);
        return "success";
    }


    @ApiOperation(value = "申请汇款卡审核通知")
    @PostMapping(value = "/remitAuditNotify")
    public String remitAuditNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(CardUrlConstants.PRIVATEKEY));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject res = JSON.parseObject(decrypt);
        // 检查回调状态
        // 检查数据信息
        // 修改数据状态
        log.info("pacificPayNotify:{}", res);
        System.out.println(res);
        return "success";
    }
}
