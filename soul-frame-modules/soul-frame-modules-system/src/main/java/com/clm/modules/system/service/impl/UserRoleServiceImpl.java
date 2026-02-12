package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.modules.system.domain.entity.UserRole;
import com.clm.modules.system.mapper.UserRoleMapper;
import com.clm.modules.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-04-28 14:03:47
 */
@Service("userRoleService")
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    
    @Override
    public void saveUserRole(Long userId, Long[] roleIds) {
        // 先删除用户与角色关联
        this.removeByUserId(userId);
        // 新增用户
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            this.save(userRole);
        }
    }

    @Override
    public void removeByUserId(Long userId) {
        this.remove(new LambdaQueryWrapper<>(UserRole.class).eq(UserRole::getUserId, userId));
    }

    @Override
    public Map<Long, List<Long>> getUserRoleMapByUserIds(List<Long> userIds) {
        return lambdaQuery().in(UserRole::getUserId, userIds)
                .list().stream().collect(
                        (HashMap::new),
                        (m, v) -> m.computeIfAbsent(v.getUserId(), k -> new ArrayList<>()).add(v.getRoleId()),
                        HashMap::putAll
                );
    }

    @Override
    public boolean hasUser(Long roleId) {
        return lambdaQuery().eq(UserRole::getRoleId,roleId).exists();
    }
}

