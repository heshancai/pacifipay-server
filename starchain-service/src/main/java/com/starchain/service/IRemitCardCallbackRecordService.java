package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.RemitCardCallbackRecord;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Service
public interface IRemitCardCallbackRecordService  extends IService<RemitCardCallbackRecord>, IMiPayNotifyServiceStrategy {

    // 自定义方法实现可以在这里定义
}
