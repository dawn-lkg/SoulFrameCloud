package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * 性能监控 实体类
 *
 * @author 陈黎明
 * @date 2025-08-19 12:30:16
 */
@Data
@TableName("performance_metrics")
public class PerformanceMetrics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "监控类型")
    @TableField("metric_type")
    private String metricType;

    @Schema(description = "监控值")
    @TableField("metric_value")
    private Double metricValue;

    @Schema(description = "采集时间")
    @TableField("collect_time")
    private Date collectTime;

    @Schema(description = "服务器id")
    @TableField("server_id")
    private String serverId;

    public PerformanceMetrics() {
        serverId = getServerIdByHostAndPid();
    }

    /**
     * 生成主机id
     */
    public static String getServerIdByHostAndPid() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            return hostname + "-" + pid;
        } catch (UnknownHostException e) {
            return "unknown-" + System.currentTimeMillis();
        }
    }

}
