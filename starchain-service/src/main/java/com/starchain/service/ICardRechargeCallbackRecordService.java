package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.CardRechargeCallbackRecord;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
public interface ICardRechargeCallbackRecordService extends IService<CardRechargeCallbackRecord>, IMiPayNotifyServiceStrategy {
    // 自定义方法可以在这里定义
}
