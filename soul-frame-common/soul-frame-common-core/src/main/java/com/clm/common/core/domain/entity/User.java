package com.clm.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
/**
 * 用户信息表(User)实体类
 *
 * @author 陈黎明
 * @since 2025-03-04 22:01:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 504939958071368869L;
            
    /**
    * 用户ID
    */
    @TableId(value = "user_id")
    private Long userId;
            
    /**
    * 部门ID
    */
    private Long deptId;
            
    /**
    * 用户账号
    */
    private String userName;
            
    /**
    * 用户昵称
    */
    private String nickName;
            
    /**
    * 用户类型（00系统用户）
    */
    private String userType;
            
    /**
    * 用户邮箱
    */
    private String email;
            
    /**
    * 手机号码
    */
    private String phone;
            
    /**
    * 用户性别（0男 1女 2未知）
    */
    private String sex;
            
    /**
    * 头像地址
    */
    private String avatar;
            
    /**
    * 密码
    */
    @JsonIgnore
    private String password;
            
    /**
    * 帐号状态（0正常 1停用）
    */
    private String status;
            
    /**
    * 最后登录IP
    */
    private String loginIp;
            
    /**
    * 最后登录时间
    */
    private Date loginDate;
}

