package com.starchain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.starchain.dao")
@SpringBootApplication
public class StarChainPacificPayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarChainPacificPayServerApplication.class, args);
    }

}
