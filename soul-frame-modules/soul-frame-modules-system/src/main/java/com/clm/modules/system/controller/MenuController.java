package com.clm.modules.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.utils.TreeUtils;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.api.system.domain.RouterDTO;
import com.clm.modules.system.domain.dto.MenuDTO;
import com.clm.modules.system.domain.param.MenuQueryParam;
import com.clm.modules.system.domain.vo.MenuVO;
import com.clm.modules.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-08 10:56:36
 */

@Tag(name = "菜单权限管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
@Validated
public class MenuController extends BaseController {
    
    private final MenuService menuService;

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "查询菜单列表")
    @GetMapping("/list")
    public Result<List<MenuVO>> list(MenuQueryParam param) {
        List<MenuVO> menus = menuService.selectMenuList(param);
        return success(menus);
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "查询菜单树")
    @GetMapping("/tree")
    public Result<List<MenuVO>> tree(MenuQueryParam param) {
        List<MenuVO> menus = menuService.selectMenuList(param);
        return success(TreeUtils.buildTree(menus, MenuVO::getMenuId, MenuVO::getParentId, MenuVO::getChildren, menu->{menu.setChildren(null);return null;}));
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取菜单详情")
    @GetMapping("/{menuId}")
    public Result<MenuVO> getInfo(@PathVariable Long menuId) {
        return success(menuService.getMenuInfo(menuId));
    }

    @Log(businessType = BusinessType.INSERT)
    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<?> add(@RequestBody @Valid MenuDTO dto) {
        menuService.addMenu(dto);
        return success();
    }

    @Log(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<?> update(@RequestBody @Valid MenuDTO dto) {
        menuService.updateMenu(dto);
        return success();
    }

    @Log(businessType = BusinessType.DELETE)
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    public Result<?> delete(@Schema(description = "菜单ID") @PathVariable Long menuId) {
        if (menuService.deleteMenu(menuId)) {
            return success();
        }
        return error("删除菜单失败");
    }

    @Log(businessType = BusinessType.CHECK)
    @Operation(summary = "检查菜单名称是否唯一")
    @GetMapping("/check-name-unique")
    public Result<Boolean> checkNameUnique(MenuDTO dto) {
        return success(menuService.checkMenuNameUnique(dto));
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取用户路由菜单")
    @GetMapping("/router/{userId}")
    public Result<List<RouterDTO>> getRouter(@PathVariable("userId") Long userId) {
        List<MenuVO> menus = menuService.selectMenuVoByUserId(userId);
        List<RouterDTO> result = menus.stream()
                .map(vo->BeanUtil.copyProperties(vo, RouterDTO.class))
                .collect(Collectors.toList());
        return success(result);
    }
}

