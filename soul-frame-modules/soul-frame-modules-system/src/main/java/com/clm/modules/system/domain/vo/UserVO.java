package com.clm.modules.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户视图对象
 *
 * @author 陈黎明
 * @since 2025-03-07
 */
@Data
public class UserVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    
    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;
    
    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userName;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;
    
    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;
    
    /**
     * 用户性别（0男 1女 2未知）
     */
    @Schema(description = "用户性别（0男 1女 2未知）")
    private String sex;
    
    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    private String avatar;
    
    /**
     * 帐号状态（0正常 1停用）
     */
    @Schema(description = "帐号状态（0正常 1停用）")
    private String status;
    
    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP")
    private String loginIp;
    
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
} 