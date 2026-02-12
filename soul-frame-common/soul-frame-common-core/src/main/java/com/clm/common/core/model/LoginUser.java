package com.clm.common.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户对象
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 角色权限标识集合
     */
    private Set<String> roles;

    /**
     * 权限标识集合
     */
    private Set<String> permissions;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;
}