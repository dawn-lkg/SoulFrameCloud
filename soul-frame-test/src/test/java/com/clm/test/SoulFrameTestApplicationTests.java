package com.clm.test;

import com.clm.api.system.RemoteLogService;
import com.clm.api.system.RemoteUserService;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class SoulFrameTestApplicationTests {


//    @Autowired
//    private RemoteUserService remoteUserService;

    @Test
    void contextLoads() {
//        Result<User> data = remoteUserService.getUserByUsername("admin");
//        System.out.println( data);

        System.out.println("hello world");
    }

}
