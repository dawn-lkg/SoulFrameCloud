package com.clm.modules.system.domain.param;


import com.clm.common.core.domain.BasePageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询参数
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperLogQueryParam extends BasePageParam {
    
    /**
     * 模块标题
     */
    @Schema(description = "模块标题")
    private String title;
    
    /**
     * 业务类型（0查询 1新增 2修改 3删除 4其他）
     */
    @Schema(description = "业务类型（0查询 1新增 2修改 3删除 4其他）")
    private Integer businessType;
    
    /**
     * 操作人员
     */
    @Schema(description = "操作人员")
    private String operName;
    
    /**
     * 操作状态（0正常 1异常）
     */
    @Schema(description = "操作状态（0正常 1异常）")
    private Integer status;
    
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginTime;
    
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endTime;

    /**
     * ip
     */
    @Schema(description = "ip")
    private String operIp;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法")
    private String requestMethod;
} 