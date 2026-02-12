package com.clm.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.common.core.domain.entity.Role;
import com.clm.modules.system.domain.dto.RoleDTO;
import com.clm.modules.system.domain.param.RoleQueryParam;
import com.clm.modules.system.domain.vo.RoleSimpleVO;
import com.clm.modules.system.domain.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * 角色信息表(Role)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-08 10:57:22
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据用户ID获取角色信息
     * @param userId 用户ID
     * @return 角色信息
     */
    List<RoleSimpleVO> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID获取角色标识
     * @param userId 用户ID
     * @return 角色标识
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleSimpleVO> getRolesByUserId(Long userId);
    
    /**
     * 分页查询角色列表
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<RoleVO> pageRoleList(RoleQueryParam param);
    
    /**
     * 根据ID获取角色详情
     * @param roleId 角色ID
     * @return 角色详情
     */
    RoleVO getRoleInfo(Long roleId);
    
    /**
     * 新增角色
     * @param param 角色参数
     * @return 是否成功
     */
    void addRole(RoleDTO param);
    
    /**
     * 修改角色
     * @param param 角色参数
     * @return 是否成功
     */
    void updateRole(RoleDTO param);
    
    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 批量删除角色
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean batchDeleteRole(List<Long> roleIds);
    
    /**
     * 修改角色状态
     * @param roleId 角色ID
     * @param status 状态
     * @return 是否成功
     */
    boolean changeRoleStatus(Long roleId, String status);
    
    /**
     * 校验角色名称是否唯一
     * @param roleName 角色名称
     * @param roleId 角色ID
     * @return 结果
     */
    boolean checkRoleNameUnique(String roleName, Long roleId);
    
    /**
     * 校验角色权限是否唯一
     * @param roleKey 角色权限
     * @param roleId 角色ID
     * @return 结果
     */
    boolean checkRoleKeyUnique(String roleKey, Long roleId);

    /**
     * 分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermission(Long roleId, List<Long> permissionIds);

    /**
     * 根据角色ID获取权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 获取用户角色标识列表
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> getRoleKeysByUserId(Long userId);
    
    /**
     * 根据角色ID获取拥有该角色的用户ID列表
     * 
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleId(Long roleId);
}

