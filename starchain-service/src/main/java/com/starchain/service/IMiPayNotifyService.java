package com.starchain.service;

import com.alibaba.fastjson2.JSON;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.common.entity.response.MiPayRemitNotifyResponse;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
public interface IMiPayNotifyService {
    /**
     * 结果回调
     */
    Boolean callBack(String callBackJson);

    default MiPayCardNotifyResponse covertToMiPayCardNotifyResponse(String callBackJson) {
        return JSON.parseObject(callBackJson, MiPayCardNotifyResponse.class);
    }
    default MiPayRemitNotifyResponse covertToMiPayRemitNotifyResponse(String callBackJson) {
        return JSON.parseObject(callBackJson, MiPayRemitNotifyResponse.class);
    }

}
