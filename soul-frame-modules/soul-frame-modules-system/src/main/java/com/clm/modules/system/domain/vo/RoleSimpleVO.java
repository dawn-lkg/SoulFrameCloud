package com.clm.modules.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * 角色信息表(Role)实体类
 *
 * @author 陈黎明
 * @since 2025-03-09 13:59:08
 */
@Data
public class RoleSimpleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -27312913647922047L;
            
    /**
    * 角色ID
    */
    private Long roleId;
            
    /**
    * 角色名称
    */
    private String roleName;
            
    /**
    * 角色权限字符串
    */
    private String roleKey;
}

