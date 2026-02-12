package com.clm.common.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Configuration
@EnableConfigurationProperties(SseProperties.class)
@RequiredArgsConstructor
public class SseAutoConfiguration {

    private final SseProperties props;

    @Bean
    public SseTemplate sseTemplate() {
        return new SseTemplate(props);
    }
}