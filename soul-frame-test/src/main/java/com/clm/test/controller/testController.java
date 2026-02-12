package com.clm.test.controller;

import com.clm.api.system.RemoteLogService;
import com.clm.api.system.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */


@RestController
@RequestMapping("/test")
public class testController {

    @Autowired
    private RemoteUserService remoteUserService;

    @GetMapping("/{username}")
    public Object hello(@PathVariable("username") String username){
        return remoteUserService.getUserByUsername(username);
    }
}
