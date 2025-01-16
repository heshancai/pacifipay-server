package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.RemitApplicationRecord;
import com.starchain.entity.dto.RemitApplicationRecordDto;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
public interface IRemitApplicationRecordService extends IService<RemitApplicationRecord> {
    /**
     * 申请汇款
     * @param remitApplicationRecordDto
     * @return
     */
    Boolean applyRemit(RemitApplicationRecordDto remitApplicationRecordDto);

    /**
     * 检查用户是否有汇款正在进行
     * @param userId
     * @param channelId
     * @return
     */
    boolean isRemitInProgress(Long userId, Long channelId);
}
