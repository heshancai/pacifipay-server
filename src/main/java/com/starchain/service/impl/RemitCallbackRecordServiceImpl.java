package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.RemitCallbackRecordMapper;
import com.starchain.entity.RemitCallbackRecord;
import com.starchain.service.IRemitCallbackRecordService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-06
 * @Description 申请汇款通知 处理类
 */
@Service
public class RemitCallbackRecordServiceImpl extends ServiceImpl<RemitCallbackRecordMapper, RemitCallbackRecord> implements IRemitCallbackRecordService {
    @Override
    public Boolean callBack(String miPayCardNotifyResponse) {
        return null;
    }
    // 自定义方法实现可以在这里定义
}
