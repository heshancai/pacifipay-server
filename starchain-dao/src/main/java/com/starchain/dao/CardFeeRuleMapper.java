package com.starchain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starchain.common.entity.CardFeeRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2025-02-07
 * @Description
 */
@Mapper
public interface CardFeeRuleMapper extends BaseMapper<CardFeeRule> {
    // 你可以在这里添加自定义查询方法
}