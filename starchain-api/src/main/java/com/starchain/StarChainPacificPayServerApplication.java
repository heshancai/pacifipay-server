package com.starchain;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "com.starchain")
@EnableDubbo
public class StarChainPacificPayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarChainPacificPayServerApplication.class, args);
    }

}
