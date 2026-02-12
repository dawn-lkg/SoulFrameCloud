package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.UserRole;

import java.util.List;
import java.util.Map;

/**
 * 用户和角色关联表(UserRole)表服务接口
 *
 * @author 陈黎明
 * @since 2025-04-28 14:03:47
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 保存用户角色关系
     *
     * @param userId 用户id
     * @param roleIds 角色id数组
     */
    void saveUserRole(Long userId, Long[] roleIds);

    /**
     * 根据用户id删除用户角色关系
     *
     * @param userId 用户id
     */
    void removeByUserId(Long userId);

    /**
     * 根据用户id列表获取用户对应角色map
     * @param userIds 用户id列表
     */
    Map<Long, List<Long> > getUserRoleMapByUserIds(List<Long> userIds);

    /**
     * 检查是否有关联用户
     * @param roleId 角色id
     * @return boolean
     */
    boolean hasUser(Long roleId);
}

