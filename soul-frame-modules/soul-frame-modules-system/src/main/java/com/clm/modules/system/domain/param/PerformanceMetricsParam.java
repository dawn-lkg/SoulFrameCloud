package com.clm.modules.system.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 性能监控 查询参数
 *
 * @author 陈黎明
 * @date 2025-08-19 12:30:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "性能监控查询参数")
public class PerformanceMetricsParam extends BasePageParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Schema(description = "")
    private Long id;

    /**
     *
     */
    @Schema(description = "")
    private String metricType;

    /**
     *
     */
    @Schema(description = "")
    private Double metricValue;

    /**
     *
     */
    @Schema(description = "")
    private Date collectTime;

    /**
     *
     */
    @Schema(description = "")
    private String serverId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String beginTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;
}
