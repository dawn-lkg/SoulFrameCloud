package com.clm.api.system.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色简略信息VO
 * 用于远程调用传输角色信息
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class RoleSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;
}
