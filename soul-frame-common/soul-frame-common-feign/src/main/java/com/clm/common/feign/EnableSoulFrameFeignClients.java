package com.clm.common.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启用 SoulFrame Feign 客户端
 * 简化配置，默认扫描 com.clm 包下的所有 Feign 接口
 *
 * @author 陈黎明
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients(basePackages = "com.clm")
public @interface EnableSoulFrameFeignClients {

    /**
     * 指定 Feign 客户端的基础包，默认为 com.clm
     */
    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackages")
    String[] value() default "com.clm";

    /**
     * 指定要扫描的 Feign 客户端类
     */
    @AliasFor(annotation = EnableFeignClients.class, attribute = "clients")
    Class<?>[] clients() default {};
}
