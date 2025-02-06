package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.CardChangeRecord;
import com.starchain.dao.CardChangeRecordMapper;
import com.starchain.service.ICardChangeRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-09
 * @Description
 */
@Service
public class CardChangeRecordServiceImpl extends ServiceImpl<CardChangeRecordMapper, CardChangeRecord> implements ICardChangeRecordService {
    // 继承 ServiceImpl，提供默认的 CRUD 实现
}