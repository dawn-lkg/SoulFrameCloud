package com.clm.api.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 菜单路由DTO
 * 用于前端路由渲染的菜单数据传输对象
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class RouterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否为外链（0-否 1-是）
     */
    private String isExternal;

    /**
     * 是否缓存（0-否 1-是）
     */
    private String isCache;

    /**
     * 菜单类型（M-目录 C-菜单 F-按钮）
     */
    private String menuType;

    /**
     * 显示状态（0-隐藏 1-显示）
     */
    private String visible;

    /**
     * 菜单状态（0-停用 1-正常）
     */
    private String status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子菜单列表
     */
    private List<RouterDTO> children;
}
