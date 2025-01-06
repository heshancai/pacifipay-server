package com.starchain.service;

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
    void callBack(MiPayCardNotifyResponse miPayCardNotifyResponse);
}
