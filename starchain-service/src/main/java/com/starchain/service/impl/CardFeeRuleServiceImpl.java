package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.CardFeeRule;
import com.starchain.dao.CardFeeRuleMapper;
import com.starchain.service.ICardFeeRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-02-07
 * @Description
 */
@Slf4j
@Service
public class CardFeeRuleServiceImpl extends ServiceImpl<CardFeeRuleMapper, CardFeeRule> implements ICardFeeRuleService {
    @Override
    public CardFeeRule getCardFeeRule(String cardCode) {
        LambdaQueryWrapper<CardFeeRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardFeeRule::getCardCode, cardCode);
        return this.getOne(queryWrapper);
    }
    // 你可以在这里实现自定义的业务方法
}