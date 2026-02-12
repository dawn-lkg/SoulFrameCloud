package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统访问记录(LoginLog)实体类
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Data
@TableName("sys_login_log")
public class LoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 访问ID
     */
    @TableId(type = IdType.AUTO)
    private Long infoId;
    
    /**
     * 用户账号
     */
    private String userName;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 登录IP地址
     */
    private String ipaddr;
    
    /**
     * 登录地点
     */
    private String loginLocation;
    
    /**
     * 浏览器类型
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 登录状态（0成功 1失败）
     */
    private String status;
    
    /**
     * 提示消息
     */
    private String msg;
    
    /**
     * 登录时间
     */
    private Date loginTime;
} 