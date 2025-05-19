package com.starchain.context;

import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.service.IMiPayNotifyServiceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author
 * @date 2025-01-06
 * @Description 策略工厂：上下文（Context）：持有一个策略对象的引用，在运行时接收或者设置具体的策略对象。 在Spring 容器中 根据类型拿到具体的 策略实现类
 */
@Component
public class MiPayNotifyStrategyFactory {
    // Spring容器注入
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * 根据 businessType 获取对应的策略实现类
     */
    public IMiPayNotifyServiceStrategy getMiPayNotifyService(String businessType) {
        MiPayNotifyType notifyType = MiPayNotifyType.getByType(businessType);
        if (notifyType == null) {
            throw new IllegalArgumentException("未知的业务类型: " + businessType);
        }
        return (IMiPayNotifyServiceStrategy) applicationContext.getBean(notifyType.getServiceName());
    }
}
