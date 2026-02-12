package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * 角色和菜单关联表(RoleMenu)实体类
 *
 * @author 陈黎明
 * @since 2025-03-08 11:01:46
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = -62808350545302516L;
            
    /**
    * 角色ID
    */
    private Long roleId;
            
    /**
    * 菜单ID
    */
    private Long menuId;
}

