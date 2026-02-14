package com.clm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置类
 * <p>
 * 在网关层统一处理跨域，避免每个微服务单独配置
 * </p>
 *
 * @author 陈黎明
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的来源 (* 生产环境建议指定具体域名)
        config.addAllowedOriginPattern("*");
        // 或者指定具体域名
        // config.addAllowedOrigin("http://localhost:5173");
        // config.addAllowedOrigin("http://localhost:3000");
        
        // 允许携带凭证（cookies、authorization header 等）
        config.setAllowCredentials(true);
        
        // 允许的请求方法
        config.addAllowedMethod("*");
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 暴露的响应头
        config.addExposedHeader("*");
        
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
