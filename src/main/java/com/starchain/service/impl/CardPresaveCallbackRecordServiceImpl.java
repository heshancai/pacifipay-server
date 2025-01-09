package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.CardPresaveCallbackRecordMapper;
import com.starchain.entity.CardPresaveCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.service.ICardPresaveCallbackRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Service
public class CardPresaveCallbackRecordServiceImpl extends ServiceImpl<CardPresaveCallbackRecordMapper, CardPresaveCallbackRecord> implements ICardPresaveCallbackRecordService {
    @Override
    public Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        return null;
    }
    // 自定义方法实现可以在这里定义
}