package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
/**
 * 用户和角色关联表(UserRole)实体类
 *
 * @author 陈黎明
 * @since 2025-04-28 14:03:46
 */
@Data
@Accessors(chain = true)
@TableName("sys_user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = -73206473653437501L;
            
    /**
    * 用户ID
    */
    private Long userId;
            
    /**
    * 角色ID
    */
    private Long roleId;
}

