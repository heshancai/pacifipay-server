package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.CardChangeRecordDao;
import com.starchain.entity.CardChangeRecord;
import com.starchain.service.ICardChangeRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-09
 * @Description
 */
@Service
public class CardChangeRecordServiceImpl extends ServiceImpl<CardChangeRecordDao, CardChangeRecord> implements ICardChangeRecordService {
    // 继承 ServiceImpl，提供默认的 CRUD 实现
}