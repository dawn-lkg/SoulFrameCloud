package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.modules.system.domain.entity.RoleMenu;
import com.clm.modules.system.mapper.RoleMenuMapper;
import com.clm.modules.system.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-08 11:01:46
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertRoleMenu(Long roleId, List<Long> menuIds) {
        if (roleId == null || CollectionUtils.isEmpty(menuIds)) {
            return;
        }

        List<RoleMenu> list = new ArrayList<>(menuIds.size());
        for (Long menuId : menuIds) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            list.add(rm);
        }

        // 批量插入
        saveBatch(list);
    }

    @Override
    public void deleteRoleMenuByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        remove(wrapper);
    }
}

