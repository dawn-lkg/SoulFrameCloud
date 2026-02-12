package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.domain.entity.Role;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.modules.system.domain.dto.RoleDTO;
import com.clm.modules.system.domain.entity.RoleMenu;
import com.clm.modules.system.domain.param.RoleQueryParam;
import com.clm.modules.system.domain.vo.RoleSimpleVO;
import com.clm.modules.system.domain.vo.RoleVO;
import com.clm.modules.system.mapper.RoleMapper;
import com.clm.modules.system.service.RoleMenuService;
import com.clm.modules.system.service.RoleService;
import com.clm.modules.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-08 10:57:22
 */
@Slf4j
@Service("roleService")
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuService roleMenuService;

    private final UserRoleService userRoleService;

    @Override
    public List<RoleSimpleVO> selectRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<RoleSimpleVO> list = baseMapper.selectRolesByUserId(userId);
        return list.stream().map(RoleSimpleVO::getRoleKey).collect(Collectors.toSet());
    }
    
    @Override
    public List<RoleSimpleVO> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public IPage<RoleVO> pageRoleList(RoleQueryParam param) {
        Page<RoleVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        IPage<RoleVO> roleVOIPage = baseMapper.selectRolePage(page, param);
        return roleVOIPage;
    }

    @Override
    public RoleVO getRoleInfo(Long roleId) {
        // 查询角色信息
        RoleVO roleVO = baseMapper.selectRoleById(roleId);
        if (roleVO == null) {
            throw new BaseException("角色不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        // 查询角色关联的菜单列表
        List<Long> menuIds = baseMapper.selectMenuIdsByRoleId(roleId);
        roleVO.setMenuIds(menuIds);
        
        return roleVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleDTO dto) {
        // 校验角色名称和权限标识是否唯一
        if (!checkRoleNameUnique(dto.getRoleName(), null)) {
            throw new BaseException("角色名称已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        if (!checkRoleKeyUnique(dto.getRoleKey(), null)) {
            throw new BaseException("角色权限标识已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        
        // 构建角色对象并保存
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        
        if(!save(role)){
            throw new BaseException("新增角色失败", HttpCodeEnum.FAILED_ADD.getCode());
        }
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            // 保存角色菜单关联
            roleMenuService.insertRoleMenu(role.getRoleId(), dto.getMenuIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO dto) {
        // 校验角色ID是否存在
        if (dto.getRoleId() == null) {
            throw new BaseException("角色ID不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        // 校验角色是否存在
        Role existRole = getById(dto.getRoleId());
        if (existRole == null) {
            throw new BaseException("角色不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        // 校验角色名称和权限标识是否唯一
        if (!checkRoleNameUnique(dto.getRoleName(), dto.getRoleId())) {
            throw new BaseException("角色名称已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        if (!checkRoleKeyUnique(dto.getRoleKey(), dto.getRoleId())) {
            throw new BaseException("角色权限标识已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        
        // 构建角色对象并更新
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        
        if(!updateById(role)){
            throw new BaseException(HttpCodeEnum.FAILED_UPDATE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 校验角色是否存在
        Role role = getById(roleId);
        if (role == null) {
            throw new BaseException("角色不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }

        //判断存在关联角色用户
        if(userRoleService.hasUser(roleId)){
            throw new BaseException("角色存在关联用户",HttpCodeEnum.ERROR.getCode());
        }
        
        // 删除角色菜单关联
        roleMenuService.deleteRoleMenuByRoleId(roleId);
        
        // 删除角色
        return removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRole(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BaseException("角色ID列表不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        // 校验角色是否已分配用户
        for (Long roleId : roleIds) {
            if (userRoleService.hasUser(roleId)) {
                throw new BaseException("角色存在关联用户", HttpCodeEnum.ERROR.getCode());
            }
        }
        
        // 批量删除角色菜单关联
        for (Long roleId : roleIds) {
            roleMenuService.deleteRoleMenuByRoleId(roleId);
        }
        
        // 批量删除角色
        return removeByIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeRoleStatus(Long roleId, String status) {
        // 校验角色是否存在
        Role role = getById(roleId);
        if (role == null) {
            throw new BaseException("角色不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        // 更新角色状态
        Role updateRole = new Role();
        updateRole.setRoleId(roleId);
        updateRole.setStatus(status);
        
        return updateById(updateRole);
    }

    @Override
    public boolean checkRoleNameUnique(String roleName, Long roleId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, roleName);
        
        Role role = getOne(queryWrapper, false);
        if (role == null) {
            return true;
        }
        
        return role.getRoleId().equals(roleId);
    }

    @Override
    public boolean checkRoleKeyUnique(String roleKey, Long roleId) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleKey, roleKey);
        
        Role role = getOne(queryWrapper, false);
        if (role == null) {
            return true;
        }
        
        return role.getRoleId().equals(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermission(Long roleId, List<Long> permissionIds) {
        roleMenuService.deleteRoleMenuByRoleId(roleId);

        if (roleId == null || CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        List<RoleMenu> roleMenus = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(permissionId);
            roleMenus.add(roleMenu);
        }
        boolean b = roleMenuService.saveBatch(roleMenus);
        if(!b){
            throw new BaseException("分配权限失败", HttpCodeEnum.FAILED_UPDATE.getCode());
        }
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public List<String> getRoleKeysByUserId(Long userId) {
        return baseMapper.selectRoleKeysByUserId(userId);
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        return baseMapper.selectUserIdsByRoleId(roleId);
    }



}

