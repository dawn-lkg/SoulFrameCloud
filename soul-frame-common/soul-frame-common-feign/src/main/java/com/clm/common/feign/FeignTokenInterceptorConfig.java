package com.clm.common.feign;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 请求拦截器配置 - 自动传递 Token 到下游服务
 *
 * @author 陈黎明
 */
@Slf4j
@Configuration
public class FeignTokenInterceptorConfig {

    /**
     * Feign 请求拦截器 - 将当前请求的 Token 传递到下游服务
     */
    @Bean
    public RequestInterceptor feignTokenInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 获取当前请求的 Token
                String token = StpUtil.getTokenValue();
                if (token != null && !token.isEmpty()) {
                    String tokenName = SaManager.getConfig().getTokenName();
                    String tokenPrefix = SaManager.getConfig().getTokenPrefix();
                    String tokenValue = StrUtil.isNotBlank(tokenPrefix)? tokenPrefix+" "+ token : token;
                    template.header(tokenName, tokenValue);
                    log.debug("Feign 传递 Token: {}", token);
                }
            }
        };
    }
}
