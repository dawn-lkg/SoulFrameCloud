package com.clm.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
public class PerformanceMetricsHourVO {
    @Schema(description = "时间")
    private Date hour;

    @Schema(description = "监控类型")
    private String metricType;

    @Schema(description = "监控指标平均值")
    private String avgValue;

    @Schema(description = "监控指标最大值")
    private String maxValue;

    @Schema(description = "监控指标最小值")
    private String minValue;

    @Schema(description = "监控指标统计数据量")
    private String count;
}
