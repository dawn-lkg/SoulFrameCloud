package com.clm.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * 角色信息表(Role)实体类
 *
 * @author 陈黎明
 * @since 2025-03-08 10:57:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 646022391932604367L;
            
    /**
    * 角色ID
    */
    @TableId
    @Schema(description = "角色ID")
    private Long roleId;
            
    /**
    * 角色名称
    */
    @Schema(description = "角色名称")
    private String roleName;
            
    /**
    * 角色权限字符串
    */
    @Schema(description = "角色权限字符串")
    private String roleKey;
            
    /**
    * 显示顺序
    */
    @Schema(description = "显示顺序")
    private Integer roleSort;
            
    /**
    * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
    */
    @Schema(description = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）")
    private String dataScope;
            
    /**
    * 菜单树选择项是否关联显示
    */
    @Schema(description = "菜单树选择项是否关联显示")
    private Integer menuCheckStrictly;
            
    /**
    * 部门树选择项是否关联显示
    */
    @Schema(description = "部门树选择项是否关联显示")
    private Integer deptCheckStrictly;
            
    /**
    * 角色状态（0正常 1停用）
    */
    @Schema(description = "角色状态（0正常 1停用）")
    private String status;
}

