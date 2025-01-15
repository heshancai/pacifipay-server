package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.RemitCancelCallbackRecordMapper;
import com.starchain.entity.RemitCancelCallbackRecord;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.service.IRemitCancelCallbackRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-06
 * @Description 汇款撤销通知
 */
@Service
public class RemitCancelCallbackRecordServiceImpl extends ServiceImpl<RemitCancelCallbackRecordMapper, RemitCancelCallbackRecord> implements IRemitCancelCallbackRecordService {
    @Override
    public Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        return null;
    }
    // 自定义方法实现可以在这里定义
}
