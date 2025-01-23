package com.starchain.callback;

import com.alibaba.fastjson2.JSON;
import com.starchain.config.PacificPayConfig;
import com.starchain.context.MiPayNotifyContext;
import com.starchain.enums.MiPayNotifyType;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.service.IMiPayNotifyService;
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
    private PacificPayConfig pacificPayConfig;

    @Autowired
    private MiPayNotifyContext miPayNotifyContext;

    @ApiOperation(value = "密付异步通知api")
    @PostMapping(value = "/miPayNotify")
    public String miPayNotify(@RequestBody String jsonObject) {
        String decrypt = null;
        try {
            decrypt = RSA2048Encrypt.decrypt(jsonObject, RSA2048Encrypt.getPrivateKey(pacificPayConfig.getPrivateKey()));
            log.info("字符串信息:{}", decrypt);
            MiPayCardNotifyResponse miPayNotifyResponse = JSON.parseObject(decrypt, MiPayCardNotifyResponse.class);
            // 参数检验
            checkRecharge(miPayNotifyResponse);
            log.info("pacificPayNotify:{}", miPayNotifyResponse);
            // 根据 businessType 获取对应的策略实现类
            IMiPayNotifyService miPayNotifyService = miPayNotifyContext.getMiPayNotifyService(miPayNotifyResponse.getBusinessType());
            Boolean callBack = miPayNotifyService.callBack(decrypt);
            if (callBack) {
                return "SUCCESS";
            }
            return "FAIL";
        } catch (Exception e) {
            log.error("数据异常,{}", e.getMessage());
            return "FAIL";
        }
    }

    /**
     * 参数校验
     *
     * @param miPayNotifyResponse
     */
    private void checkRecharge(MiPayCardNotifyResponse miPayNotifyResponse) {
        Assert.notNull(miPayNotifyResponse, "回调信息为空");
        Assert.hasText(miPayNotifyResponse.getNotifyId(), "通知ID不能为空");
        if (!miPayNotifyResponse.getBusinessType().equals(MiPayNotifyType.Remit.getType())) {
            Assert.hasText(miPayNotifyResponse.getCardId(), "卡ID不能为空");
        }
        Assert.hasText(miPayNotifyResponse.getStatus(), "状态不能为空");
        Assert.hasText(miPayNotifyResponse.getStatusDesc(), "状态描述不能为空");
    }
}
