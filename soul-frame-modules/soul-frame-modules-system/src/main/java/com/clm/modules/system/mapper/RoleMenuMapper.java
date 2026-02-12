package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.modules.system.domain.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author 陈黎明
 * @since 2025-03-08 11:01:46
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    
    /**
     * 批量新增角色菜单关联
     *
     * @param list 角色菜单关联列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(@Param("list") List<RoleMenu> list);
}

