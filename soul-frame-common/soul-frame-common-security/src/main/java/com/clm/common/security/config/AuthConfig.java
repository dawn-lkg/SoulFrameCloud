package com.clm.common.security.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Sa-Token 配置类（用于 Servlet 环境：Auth、System 等服务）
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Configuration
public class AuthConfig {

    /**
     * 配置 Sa-Token Dao（使用 Redis 存储）
     */
    @Primary
    @Bean
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoRedisJackson();
    }

    /**
     * 配置 Sa-Token 使用 JWT 模式
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
