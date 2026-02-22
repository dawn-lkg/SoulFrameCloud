package com.clm.modules.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.clm.common.feign.EnableSoulFrameFeignClients;

/**
 * 系统服务启动类
 */
@EnableDiscoveryClient
@EnableSoulFrameFeignClients
@SpringBootApplication
@MapperScan("com.clm.modules.system.mapper")
public class SoulFrameModulesSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameModulesSystemApplication.class, args);
    }

}
