package com.clm.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.clm.common.feign.EnableSoulFrameFeignClients;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
@EnableSoulFrameFeignClients
@EnableAutoDataSourceProxy
@MapperScan(
    basePackages = "com.clm.test.mapper"
)
public class SoulFrameTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoulFrameTestApplication.class, args);
    }
}
