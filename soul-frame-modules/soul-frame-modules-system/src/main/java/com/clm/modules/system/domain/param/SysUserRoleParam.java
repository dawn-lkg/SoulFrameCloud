package com.clm.modules.system.domain.param;

import lombok.Data;

/**
 * 用户和角色关联表新增/编辑参数
 *
 * @author 陈黎明
 * @since 
 */
@Data
public class SysUserRoleParam {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
}
