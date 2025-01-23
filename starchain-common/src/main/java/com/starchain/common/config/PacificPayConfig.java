package com.starchain.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @date 2024-12-31
 * @Description 与银行卡端交互需要的信息
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class PacificPayConfig {
    // 自己生成的公钥
    private String publicKey;
    // 自己生成的私钥
    private String privateKey;
    // 银行卡端提供
    private String id;
    // 银行卡端提供
    private String secret;
    // 银行卡端提供
    private String baseUrl;
    // 银行卡端提供(银行卡端公钥)
    private String serverPublicKey;
}
