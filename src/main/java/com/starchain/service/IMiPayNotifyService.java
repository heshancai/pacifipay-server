package com.starchain.service;

import com.starchain.entity.RemitCardNotify;
import com.starchain.entity.response.MiPayCardNotifyResponse;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
public interface IMiPayNotifyService {
    /**
     * 结果回调
     */
    Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse);

}
