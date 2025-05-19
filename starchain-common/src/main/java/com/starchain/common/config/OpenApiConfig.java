package com.starchain.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @date 2025-05-19
 * @Description
 */
@Configuration
public class OpenApiConfig {
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API文档")
                .version("1.0")
                .description("项目API文档"));
    }
}
