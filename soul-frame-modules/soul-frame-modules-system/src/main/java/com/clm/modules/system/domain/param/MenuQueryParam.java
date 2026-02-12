package com.clm.modules.system.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单查询参数
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class MenuQueryParam {
    
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;
    
    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    
    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;
} 