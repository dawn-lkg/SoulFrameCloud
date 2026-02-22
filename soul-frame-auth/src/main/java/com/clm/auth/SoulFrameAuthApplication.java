package com.clm.auth;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.clm.common.feign.EnableSoulFrameFeignClients;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration;

import jakarta.annotation.PostConstruct;

@EnableDiscoveryClient
@EnableSoulFrameFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, Knife4jAutoConfiguration.class})
public class SoulFrameAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameAuthApplication.class, args);
    }
}
