package com.clm.common.core.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Constants {
    
    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";
    
    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "Success";
    
    /**
     * 注销
     */
    String LOGOUT = "Logout";
    
    /**
     * 注册
     */
    String REGISTER = "Register";
    
    /**
     * 登录失败
     */
    String LOGIN_FAIL = "Error";
    
    /**
     * 验证码 redis key
     */
    String CAPTCHA_CODE_KEY = "captcha_codes:";
    
    /**
     * 验证码有效期（分钟）
     */
    Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 字典状态
     */
    String DICT_NORMAL = "0";

    /**
     * 登录用户 redis key
     */
    public static final String SEC_WEBSOCKET_PROTOCOL_HEADER = "sec-websocket-protocol";

    // Object类的默认方法集合
    Set<String> OBJECT_METHODS = new HashSet<>(Arrays.asList(
            "equals", "hashCode", "toString", "getClass", "notify", "notifyAll", "wait"
    ));
} 