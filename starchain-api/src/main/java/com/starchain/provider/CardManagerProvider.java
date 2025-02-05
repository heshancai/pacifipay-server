package com.starchain.provider;

import com.starchain.common.dubbo.ICardManagerProvider;
import com.starchain.common.result.ClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author
 * @date 2025-01-27
 * @Description 查询
 */
@Slf4j
@DubboService(version = "1.0") // 提供和消费者需要指定同一个version
public class CardManagerProvider implements ICardManagerProvider {
    @Override
    public ClientResponse queryCardManagePage() {
        return null;
    }
}
