package com.clm.api.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线用户DTO
 * 用于远程调用记录在线用户信息
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class OnlineUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录IP
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * Token
     */
    private String token;
}
