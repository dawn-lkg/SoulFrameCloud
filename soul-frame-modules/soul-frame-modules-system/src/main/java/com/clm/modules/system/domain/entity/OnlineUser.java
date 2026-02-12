package com.clm.modules.system.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线用户实体类
 *
 * @author 陈黎明
 * @since 2025-03-11
 */
@Data
public class OnlineUser implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 会话编号
     */
    @Schema(description = "会话编号")
    private String tokenId;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String userName;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;
    
    /**
     * 登录IP地址
     */
    @Schema(description = "登录IP地址")
    private String ipaddr;
    
    /**
     * 登录地点
     */
    @Schema(description = "登录地点")
    private String loginLocation;
    
    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器类型")
    private String browser;
    
    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;
    
    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
} 