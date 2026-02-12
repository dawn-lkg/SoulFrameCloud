package com.clm.modules.system.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门查询参数
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Data
@Schema(description = "部门查询参数")
public class DeptQueryParam {

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "部门状态（0正常 1停用）")
    private String status;
}
