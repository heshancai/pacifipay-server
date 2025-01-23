package com.starchain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starchain.common.entity.RemitCardCallbackRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2025-01-06
 * @Description 申请汇款卡审核通知
 */
@Mapper

public interface RemitCardCallbackRecordMapper extends BaseMapper<RemitCardCallbackRecord> {
}
