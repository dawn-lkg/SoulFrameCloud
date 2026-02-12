package com.clm.common.sse;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@ConfigurationProperties(prefix = "sse")
@Data
public class SseProperties {
    private Duration defaultTimeout = Duration.ZERO;
    private Duration heartbeat = Duration.ofSeconds(45);
    private int maxRetry = 3;
}