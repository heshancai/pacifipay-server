package com.starchain.context;

import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.service.IMiPayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author
 * @date 2025-01-06
 * @Description 支持MiPay回调通知的上下文
 */
@Component
public class MiPayNotifyContext {
    // Spring容器注入
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * 根据 businessType 获取对应的策略实现类
     */
    public IMiPayNotifyService getMiPayNotifyService(String businessType) {
        MiPayNotifyType notifyType = MiPayNotifyType.getByType(businessType);
        if (notifyType == null) {
            throw new IllegalArgumentException("未知的业务类型: " + businessType);
        }
        return (IMiPayNotifyService) applicationContext.getBean(notifyType.getServiceName());
    }
}
