package com.clm.modules.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 菜单视图对象
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class MenuVO {
    
    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID")
    private Long menuId;
    
    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String menuName;
    
    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
    private Long parentId;
    
    /**
     * 父菜单名称
     */
    @Schema(description = "父菜单名称")
    private String parentName;
    
    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer orderNum;
    
    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String path;
    
    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;
    
    /**
     * 路由参数
     */
    @Schema(description = "路由参数")
    private String query;
    
    /**
     * 是否为外链（0组件 1外联 2内链）
     */
    @Schema(description = "是否为外链（0组件 1外联 2内链）")
    private Integer isFrame;
    
    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Schema(description = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;
    
    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;
    
    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Schema(description = "菜单状态（0显示 1隐藏）")
    private String visible;
    
    /**
     * 菜单状态（0正常 1停用）
     */
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;
    
    /**
     * 权限标识
     */
    @Schema(description = "权限标识")
    private String perms;
    
    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 子菜单
     */
    @Schema(description = "子菜单")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MenuVO> children = new ArrayList<>();
}
