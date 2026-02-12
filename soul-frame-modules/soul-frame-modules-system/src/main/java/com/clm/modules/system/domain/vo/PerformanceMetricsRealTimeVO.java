package com.clm.modules.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class PerformanceMetricsRealTimeVO {
    /**
     * cpu使用率时间列表
     */
    private List<String> cpuTimeList;

    /**
     * cpu使用率列表
     */
    private List<String> cpuUsageList;

    /**
     * 内存使用率时间列表
     */
    private List<String> memTimeList;

    /**
     * 内存使用率列表
     */
    private List<String> memUsageList;
}
