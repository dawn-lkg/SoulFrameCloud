package com.clm.modules.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class SoulFrameModulesFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameModulesFileApplication.class, args);
    }

}
