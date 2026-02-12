package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * 角色和部门关联表(RoleDept)实体类
 *
 * @author 陈黎明
 * @since 2025-03-08 11:02:08
 */
@Data
@TableName("sys_role_dept")
public class RoleDept  implements Serializable {

    @Serial
    private static final long serialVersionUID = -56806592249724639L;
            
    /**
    * 角色ID
    */
    private Long roleId;
            
    /**
    * 部门ID
    */
    private Long deptId;
}

