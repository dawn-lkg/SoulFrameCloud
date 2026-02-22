package com.clm.modules.file;

import com.clm.common.feign.EnableSoulFrameFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@EnableSoulFrameFeignClients
@SpringBootApplication
@MapperScan("com.clm.modules.file.mapper")
public class SoulFrameModulesFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameModulesFileApplication.class, args);
    }

}
