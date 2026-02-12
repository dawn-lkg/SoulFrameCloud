package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.domain.entity.Menu;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.modules.system.domain.dto.MenuDTO;
import com.clm.modules.system.domain.param.MenuQueryParam;
import com.clm.modules.system.domain.vo.MenuVO;
import com.clm.modules.system.mapper.MenuMapper;
import com.clm.modules.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author 陈黎明
 * @since 2025-03-08 10:56:36
 */
@Service("menuService")
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Override
    public List<MenuVO> selectMenuVoByUserId(Long userId) {
        return baseMapper.selectMenuVoByUserId(userId);
    }
    
    @Override
    public List<MenuVO> selectMenuList(MenuQueryParam param) {
        return baseMapper.selectMenuList(param);
    }
    
    /**
     * 得到子节点列表
     */
    private List<MenuVO> getChildList(List<MenuVO> list, MenuVO t) {
        List<MenuVO> children = new ArrayList<>();
        for (MenuVO menu : list) {
            if (menu.getParentId().equals(t.getMenuId())) {
                children.add(menu);
            }
        }
        return children;
    }
    
    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<MenuVO> list, MenuVO t) {
        return getChildList(list, t).size() > 0;
    }
    
    @Override
    public MenuVO getMenuInfo(Long menuId) {
        MenuVO menuVo = baseMapper.selectMenuById(menuId);
        if (menuVo == null) {
            throw new BaseException("菜单不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        return menuVo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(MenuDTO dto) {
        // 校验菜单名称是否唯一
        if (!checkMenuNameUnique(dto)) {
            throw new BaseException("菜单名称已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        
        if(!save(menu)){
            throw new BaseException("新增菜单失败", HttpCodeEnum.FAILED_ADD.getCode());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuDTO dto) {
        // 校验菜单ID是否为空
        if (dto.getMenuId() == null) {
            throw new BaseException("菜单ID不能为空", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        // 校验菜单是否存在
        Menu existMenu = getById(dto.getMenuId());
        if (existMenu == null) {
            throw new BaseException("菜单不存在", HttpCodeEnum.DATA_NOT_EXIST.getCode());
        }
        
        // 校验菜单名称是否唯一
        if (!checkMenuNameUnique(dto)) {
            throw new BaseException("菜单名称已存在", HttpCodeEnum.DATA_EXIST.getCode());
        }
        
        // 不能将自己设置为自己的父节点
        if (dto.getMenuId().equals(dto.getParentId())) {
            throw new BaseException("上级菜单不能选择自己", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        
        if(!updateById(menu)){
            throw new BaseException("修改菜单失败", HttpCodeEnum.FAILED_UPDATE.getCode());
        }

    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long menuId) {
        // 校验是否存在子菜单
        if (baseMapper.hasChildByMenuId(menuId) > 0) {
            throw new BaseException("菜单存在子菜单", HttpCodeEnum.BAD_REQUEST.getCode());
        }
        
        // 校验是否已分配角色
        if (baseMapper.checkMenuExistRole(menuId) > 0) {
            if (baseMapper.deleteMenuRoleByMenuId(menuId) <= 0) {
                throw new BaseException("删除失败", HttpCodeEnum.BAD_REQUEST.getCode());
            }
        }
        
        return removeById(menuId);
    }
    
    @Override
    public boolean checkMenuNameUnique(MenuDTO dto) {
        Long menuId = dto.getMenuId() == null ? -1L : dto.getMenuId();
        
        // 判断同一级别下是否有重名菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuName, dto.getMenuName())
                   .ne(Objects.nonNull(dto.getMenuId()),Menu::getMenuId, menuId)
                   .eq(Menu::getParentId, dto.getParentId());
        
        Menu menu = getOne(queryWrapper);
        
        return menu == null || menu.getMenuId().equals(menuId);
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return baseMapper.selectPermsByUserId(userId);
    }


    @Override
    public List<Long> getRoleIdsByMenuId(Long menuId) {
        return baseMapper.selectRoleIdsByMenuId(menuId);
    }
}

