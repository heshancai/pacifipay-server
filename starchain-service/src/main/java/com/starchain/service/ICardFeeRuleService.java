package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.CardFeeRule;

/**
 * @author
 * @date 2025-02-07
 * @Description
 */
public interface ICardFeeRuleService extends IService<CardFeeRule> {
    /**
     * 根据卡类型编码获取卡费规则
     * @param cardCode
     * @return
     */
    CardFeeRule getCardFeeRule(String cardCode);
    // 你可以在这里定义自定义业务逻辑方法
}
