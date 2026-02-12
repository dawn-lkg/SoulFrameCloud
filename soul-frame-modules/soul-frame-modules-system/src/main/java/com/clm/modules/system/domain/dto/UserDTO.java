package com.clm.modules.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户数据传输对象
 *
 * @author 陈黎明
 * @since 2025-03-07
 */
@Data
public class UserDTO {

    /**
     * 用户ID（编辑时必填）
     */
    private Long userId;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 用户账号
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    /**
     * 用户昵称
     */
    private String nickName;
    
    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;
    
    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;
    
    /**
     * 密码（新增时必填）
     */
    private String password;
    
    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;
    /**
     * 角色ID列表
     */
    private Long[] roleIds;
} 