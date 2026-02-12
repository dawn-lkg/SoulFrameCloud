package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.common.core.domain.entity.Menu;
import com.clm.modules.system.domain.dto.MenuDTO;
import com.clm.modules.system.domain.param.MenuQueryParam;
import com.clm.modules.system.domain.vo.MenuVO;

import java.util.List;

/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author 陈黎明
 * @since 2025-03-08 10:56:36
 */
public interface MenuService extends IService<Menu> {
    
    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuVO> selectMenuVoByUserId(Long userId);
    
    /**
     * 查询菜单列表
     *
     * @param param 查询参数
     * @return 菜单列表
     */
    List<MenuVO> selectMenuList(MenuQueryParam param);
    
    /**
     * 根据ID查询菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    MenuVO getMenuInfo(Long menuId);
    
    /**
     * 新增菜单
     *
     * @param dto 菜单参数
     * @return 结果
     */
    void addMenu(MenuDTO dto);
    
    /**
     * 修改菜单
     *
     * @param dto 菜单参数
     * @return 结果
     */
    void updateMenu(MenuDTO dto);
    
    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    boolean deleteMenu(Long menuId);
    
    /**
     * 校验菜单名称是否唯一
     *
     * @param dto 菜单参数
     * @return 结果
     */
    boolean checkMenuNameUnique(MenuDTO dto);

    /**
     * 查询用户拥有权限标识
     * @param userId 用户ID
     */
    List<String> getPermissionsByUserId(Long userId);
    
    /**
     * 根据菜单ID获取关联的角色ID列表
     *
     * @param menuId 菜单ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByMenuId(Long menuId);
}

