package com.clm.api.system.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录日志DTO
 * 用于远程调用记录登录日志
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class LoginLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 登录状态（0-失败 1-成功）
     */
    private Integer status;

    /**
     * 登录IP
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;
}
