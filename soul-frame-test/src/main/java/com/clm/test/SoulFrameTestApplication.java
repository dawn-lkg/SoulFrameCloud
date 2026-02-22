package com.clm.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.clm.common.feign.EnableClmFeignClients;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableClmFeignClients
public class SoulFrameTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameTestApplication.class, args);
    }

}
