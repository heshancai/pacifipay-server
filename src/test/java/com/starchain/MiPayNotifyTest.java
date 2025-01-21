package com.starchain;

/**
 * @author
 * @date 2025-01-14
 * @Description
 */

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.callBack.MiPayNotifyController;
import com.starchain.config.PacificPayConfig;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.entity.response.MiPayRemitNotifyResponse;
import com.starchain.util.RSA2048Encrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class MiPayNotifyTest {
    @Autowired
    private PacificPayConfig pacificPayConfig;
    @Autowired
    private MiPayNotifyController miPayNotifyController;
    // 测试miPay 卡充值回调
    @Test
    void miPayNotify() {
        MiPayCardNotifyResponse response = new MiPayCardNotifyResponse();
        response.setNotifyId("20250114112813cfc44");
        response.setBusinessType("CardRecharge");
        response.setStatus("SUCCESS");
        response.setStatusDesc("CardRecharge Success");
        response.setCardId("2025010910301416050");
        response.setCardCode("TpyMDN6");
        response.setCardNo("5258470882100335");
        response.setMchOrderId("1879526305016315904");

        JSONObject amount = new JSONObject();
        amount.put("actual", new BigDecimal("10.00"));
        amount.put("recharge", new BigDecimal("10.00"));
        amount.put("handleFee", new BigDecimal("0.00"));
        response.setAmount(amount);

        String jsonString = JSON.toJSONString(response);

        // 使用公钥进行加密
        try {
            String encrypt = RSA2048Encrypt.encrypt(jsonString, RSA2048Encrypt.getPublicKey(pacificPayConfig.getPublicKey()));
            miPayNotifyController.miPayNotify(encrypt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 测试miPay 申请汇款通知
    @Test
    void Remit() {
        MiPayRemitNotifyResponse response = new MiPayRemitNotifyResponse();
        response.setNotifyId("202501211528599ed13");
        response.setOrderId("10000987654202501211458594330d8b4a");
        response.setBusinessType("Remit");
        response.setStatus("SUCCESS");
        response.setStatusDesc("Remit Success");
        response.setTradeId("20250121145859dcc46");
        response.setRemitCode("UQR_CNH");

        String jsonString = JSON.toJSONString(response);

        // 使用公钥进行加密
        try {
            String encrypt = RSA2048Encrypt.encrypt(jsonString, RSA2048Encrypt.getPublicKey(pacificPayConfig.getPublicKey()));
            miPayNotifyController.miPayNotify(encrypt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
