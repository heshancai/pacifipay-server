package com.starchain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starchain.common.entity.CardChangeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2025-01-09
 * @Description
 */
@Mapper

public interface CardChangeRecordMapper extends BaseMapper<CardChangeRecord> {
    // 继承 BaseMapper，提供基础的 CRUD 操作
}
