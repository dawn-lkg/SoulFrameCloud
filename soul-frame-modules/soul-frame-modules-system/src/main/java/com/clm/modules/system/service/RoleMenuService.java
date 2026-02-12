package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.RoleMenu;

import java.util.List;

/**
 * 角色和菜单关联表(RoleMenu)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-08 11:01:46
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 批量插入角色和菜单关联表
     *
     * @param roleId
     * @param menuIds
     */
    void insertRoleMenu(Long roleId, List<Long> menuIds);

    /**
     * 根据角色ID删除角色和菜单关联表
     * @param roleId
     */
    void deleteRoleMenuByRoleId(Long roleId);
}

