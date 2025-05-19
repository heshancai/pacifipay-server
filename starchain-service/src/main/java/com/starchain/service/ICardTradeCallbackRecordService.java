package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.CardTradeCallbackRecord;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
public interface ICardTradeCallbackRecordService extends IService<CardTradeCallbackRecord>, IMiPayNotifyServiceStrategy {
    // 自定义方法可以在这里定义
}
