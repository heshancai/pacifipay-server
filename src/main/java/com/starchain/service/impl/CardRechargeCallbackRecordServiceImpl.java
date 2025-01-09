package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.CardRechargeCallbackRecordMapper;
import com.starchain.entity.CardRechargeCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.service.ICardRechargeCallbackRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Service
public class CardRechargeCallbackRecordServiceImpl extends ServiceImpl<CardRechargeCallbackRecordMapper, CardRechargeCallbackRecord> implements ICardRechargeCallbackRecordService {
    @Override
    public Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        return null;
    }
    // 自定义方法实现可以在这里定义
}
