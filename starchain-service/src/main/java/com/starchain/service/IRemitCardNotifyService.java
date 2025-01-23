package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.RemitCardNotify;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
public interface IRemitCardNotifyService extends IService<RemitCardNotify> {
    /**
     * 回调记录是否存在
     * @param miPayNotifyResponse
     * @return
     */
    RemitCardNotify checkDepositRecordIsExist(MiPayCardNotifyResponse miPayNotifyResponse);
}
