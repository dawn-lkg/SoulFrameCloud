package com.clm.common.feign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 配置属性
 * 在业务模块的 Nacos 配置中添加：
 * feign:
 *   client:
 *     config:
 *       default:
 *         connect-timeout: 5000
 *         read-timeout: 5000
 *
 * @author 陈黎明
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "feign.client.config")
public class FeignClientProperties {

    /**
     * 默认配置
     */
    private DefaultConfig defaultConfig = new DefaultConfig();

    @Data
    public static class DefaultConfig {
        /**
         * 连接超时时间
         */
        private int connectTimeout = 5000;

        /**
         * 读取超时时间
         */
        private int readTimeout = 5000;
    }
}
