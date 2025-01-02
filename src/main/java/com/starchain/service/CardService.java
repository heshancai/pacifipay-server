package com.starchain.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.util.TpyshUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@Service
public class CardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    /**
     * 查询商户余额
     *
     * @return
     */
    public JSONObject mchInfo() {
        String token = null;
        try {
            token = TpyshUtils.getToken(CardUrlConstants.BASEURL, CardUrlConstants.APPID, CardUrlConstants.APPSECRET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.mchInfo, token, "",
                pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        return JSON.parseObject(str);
    }
}
