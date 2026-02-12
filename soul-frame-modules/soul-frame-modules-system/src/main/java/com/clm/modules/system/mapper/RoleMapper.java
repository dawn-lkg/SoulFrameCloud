package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.domain.entity.Role;
import com.clm.modules.system.domain.param.RoleQueryParam;
import com.clm.modules.system.domain.vo.RoleSimpleVO;
import com.clm.modules.system.domain.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-08 10:57:22
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleSimpleVO> selectRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询角色权限标识
     *
     * @param userId 用户ID
     * @return 角色权限标识
     */
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询拥有该角色的用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 分页查询角色列表
     *
     * @param page  分页参数
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<RoleVO> selectRolePage(Page<RoleVO> page, @Param("param") RoleQueryParam param);
    
    /**
     * 根据ID查询角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    RoleVO selectRoleById(@Param("roleId") Long roleId);
    
    /**
     * 查询角色关联的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}

