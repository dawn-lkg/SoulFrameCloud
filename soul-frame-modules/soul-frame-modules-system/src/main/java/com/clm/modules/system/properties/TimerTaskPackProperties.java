package com.clm.modules.system.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@Component
@ConfigurationProperties(prefix = "timer-task")
public class TimerTaskPackProperties {
    private List<String> pack;
}
