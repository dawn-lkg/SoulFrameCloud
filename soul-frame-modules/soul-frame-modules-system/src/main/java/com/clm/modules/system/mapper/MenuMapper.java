package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.common.core.domain.entity.Menu;
import com.clm.modules.system.domain.param.MenuQueryParam;
import com.clm.modules.system.domain.vo.MenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-08 10:56:36
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户id查询菜单
     *
     * @param userId 用户id
     * @return 菜单列表
     */
    List<MenuVO> selectMenuVoByUserId(@Param("userId") Long userId);
    
    /**
     * 查询菜单列表
     *
     * @param param 查询参数
     * @return 菜单列表
     */
    List<MenuVO> selectMenuList(@Param("param") MenuQueryParam param);
    
    /**
     * 根据ID查询菜单
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    MenuVO selectMenuById(@Param("menuId") Long menuId);
    
    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);
    
    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    int hasChildByMenuId(@Param("menuId") Long menuId);

    /**
     * 删除菜单和角色关联
     */
    int deleteMenuRoleByMenuId(@Param("menuId") Long menuId);

    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    int checkMenuExistRole(@Param("menuId") Long menuId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据菜单ID查询关联的角色ID列表
     * @param menuId 菜单ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByMenuId(@Param("menuId") Long menuId);
}
