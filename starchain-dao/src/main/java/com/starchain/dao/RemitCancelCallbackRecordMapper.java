package com.starchain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starchain.entity.RemitCancelCallbackRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2025-01-06
 * @Description 汇款撤销
 */
@Mapper

public interface RemitCancelCallbackRecordMapper extends BaseMapper<RemitCancelCallbackRecord> {
}
