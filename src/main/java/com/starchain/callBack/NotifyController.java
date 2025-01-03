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
 * @date 2024-12-18
 * @Description 密付异步通知api
 */
@Slf4j
@RestController
@RequestMapping("/notify")
public class NotifyController {



    @ApiOperation(value = "卡开通通知")
    @PostMapping(value = "/miPayNotify")
    public String cardOpenNotify(@RequestBody String jsonObject) {
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
