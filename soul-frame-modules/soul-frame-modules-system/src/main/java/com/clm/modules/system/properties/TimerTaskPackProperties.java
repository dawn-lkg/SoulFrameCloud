package com.clm.modules.system.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务包扫描配置
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@Component
@ConfigurationProperties(prefix = "timer-task")
public class TimerTaskPackProperties {
    /**
     * 需要扫描的定时任务包路径
     */
    private List<String> pack = new ArrayList<>();
}
