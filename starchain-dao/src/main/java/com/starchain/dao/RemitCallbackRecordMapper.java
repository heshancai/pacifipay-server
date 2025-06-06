package com.starchain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starchain.common.entity.RemitCallbackRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2025-01-06
 * @Description 申请汇款通知
 */
@Mapper

public interface RemitCallbackRecordMapper extends BaseMapper<RemitCallbackRecord> {
    // 自定义方法可以在这里定义
}
