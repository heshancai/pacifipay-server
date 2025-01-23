package com.starchain.provider;

import com.starchain.common.dubbo.ITestDubboService;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author
 * @date 2025-01-23
 * @Description
 */
@DubboService(version = "1.0") // 提供和消费者需要指定同一个version
@Slf4j
public class TestDubboServiceProvider implements ITestDubboService {

    @Override
    public ClientResponse test() {
        return ResultGenerator.genSuccessResult("test");
    }
}
