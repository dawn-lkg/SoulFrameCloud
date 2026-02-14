package com.clm.modules.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.api.system.domain.RoleSimpleDTO;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.Role;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.modules.system.domain.dto.RoleDTO;
import com.clm.modules.system.domain.param.RoleQueryParam;
import com.clm.modules.system.domain.vo.RoleSimpleVO;
import com.clm.modules.system.domain.vo.RoleVO;
import com.clm.modules.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息表(Role)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-08 10:57:22
 */

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Validated
public class RoleController extends BaseController {
    
    private final RoleService roleService;

//    @SaCheckPermission("system:role:query")
    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public Result<IPage<RoleVO>> page(RoleQueryParam param) {
        return success(roleService.pageRoleList(param));
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return success(roleService.list());
    }


    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取角色详情")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getInfo(@Schema(description = "角色ID") @PathVariable("roleId") Long roleId) {
        return success(roleService.getRoleInfo(roleId));
    }

    @Log(businessType = BusinessType.INSERT)
    @Operation(summary = "新增角色")
    @PostMapping
    public Result<?> add(@RequestBody @Valid RoleDTO param) {
        roleService.addRole(param);
        return success();
    }

    @Log(businessType = BusinessType.UPDATE)
    @Operation(summary = "修改角色")
    @PutMapping
    public Result<?> update(@RequestBody @Valid RoleDTO param) {
        roleService.updateRole(param);
        return success();
    }

    @Log(businessType = BusinessType.DELETE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<?> delete(@Schema(description = "角色ID") @PathVariable("roleId") Long roleId) {
        if (roleService.deleteRole(roleId)) {
            return success();
        }
        return error("删除角色失败");
    }

    @Log(businessType = BusinessType.DELETE)
    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Long> roleIds) {
        if (roleService.batchDeleteRole(roleIds)) {
            return success();
        }
        return error("批量删除角色失败");
    }

    @Log("修改角色状态")
    @Operation(summary = "修改角色状态")
    @PutMapping("/{roleId}/status/{status}")
    public Result<?> changeStatus(@Schema(description = "角色ID") @PathVariable("roleId") Long roleId,@Schema(description = "角色状态") @PathVariable("status") String status) {
        if (roleService.changeRoleStatus(roleId, status)) {
            return success();
        }
        return error("修改角色状态失败");
    }

    @Log
    @Operation(summary = "校验角色名称是否唯一")
    @GetMapping("/check-name-unique")
    public Result<Boolean> checkNameUnique(@Schema(description = "角色名称") String roleName,@Schema(description = "角色ID") Long roleId) {
        return success(roleService.checkRoleNameUnique(roleName, roleId));
    }

    @Log
    @Operation(summary = "校验角色权限是否唯一")
    @GetMapping("/check-key-unique")
    public Result<Boolean> checkKeyUnique(@Schema(description = "角色权限") String roleKey,@Schema(description = "角色ID") Long roleId) {
        return success(roleService.checkRoleKeyUnique(roleKey, roleId));
    }

    @Log
    @Operation(summary = "分配权限")
    @PutMapping("/{roleId}/permission")
    public Result<?> assignPermission(@Schema(description = "角色ID") @PathVariable("roleId") Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermission(roleId, permissionIds);
        return success();
    }

    @Log
    @Operation(summary = "根据角色id查询菜单id列表")
    @GetMapping("/{roleId}/menu")
    public Result<List<Long>> getRoleMenu(@Schema(description = "角色ID") @PathVariable("roleId") Long roleId) {
        return success(roleService.getPermissionIdsByRoleId(roleId));
    }

    @Log
    @Operation(summary = "根据用户ID查询角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<RoleSimpleDTO>> selectRolesByUserId(@Schema(description = "用户ID") @PathVariable("userId") Long userId) {
        List<RoleSimpleVO> roleSimpleVOS = roleService.selectRolesByUserId(userId);
        return success(BeanUtil.copyToList(roleSimpleVOS, RoleSimpleDTO.class));
    }
}

