package com.clm.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.User;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.modules.system.domain.dto.PasswordDTO;
import com.clm.modules.system.domain.dto.UserDTO;
import com.clm.modules.system.domain.param.UserQueryParam;
import com.clm.modules.system.domain.vo.UserPageVO;
import com.clm.modules.system.domain.vo.UserSelectVO;
import com.clm.modules.system.domain.vo.UserVO;
import com.clm.modules.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户信息表(User)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-07
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @Operation(summary = "获取用户分页列表")
    @Log(businessType = BusinessType.QUERY)
    @SaCheckPermission("system:user:query")
    @GetMapping("/page")
    public Result<IPage<UserPageVO>> page(@Valid UserQueryParam param) {
        return success(userService.getUserPage(param));
    }

    @Operation(summary = "获取用户详细信息")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/{userId}")
    public Result<UserVO> info(@Schema(description = "用户ID") @PathVariable("userId") Long userId) {
        return success(userService.getUserInfo(userId));
    }

    @Operation(summary = "新增用户")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping
    public Result<?> add(@RequestBody @Valid UserDTO userDTO) {
        userService.addUser(userDTO);
        return success();
    }

    @Operation(summary = "根据账号查询用户")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/info/{username}")
    public Result<User> info(@Schema(description = "用户账号") @PathVariable("username") String username) {
        return success(userService.getUserByUsername(username));
    }

    @Operation(summary = "修改用户")
    @Log(value = "修改用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<?> update(@RequestBody @Valid UserDTO userDTO) {
        userService.updateUser(userDTO);
        return success();
    }

    @Operation(summary = "修改用户信息")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/updateUserInfo")
    public Result<?> updateUserInfo(@RequestBody @Valid UserDTO userDTO) {
        userService.updateUserInfo(userDTO);
        return success();
    }

    @Operation(summary = "更新头像")
    @Log(businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvatar")
    public Result<?> updateAvatar(MultipartFile file) {
        userService.updateAvatar(file);
        return success();
    }

    @Operation(summary = "修改用户密码")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody @Valid PasswordDTO passwordDTO) {
        userService.updatePassword(passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return success();
    }

    @Operation(summary = "删除用户")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{userId}")
    public Result<?> delete(@Schema(description = "用户ID") @PathVariable Long userId) {
        userService.deleteUser(userId);
        return success();
    }

    @Operation(summary = "批量删除用户")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody @Schema(description = "用户ID列表") List<Long> userIds) {
        userService.batchDeleteUser(userIds);
        return success();
    }

    @Operation(summary = "检查用户名是否存在")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/checkUsername/{username}")
    public Result<?> checkUsernameExists(@Schema(description = "用户名") @PathVariable String username) {
        return success(userService.checkUsernameExists(username, null));
    }

    @Operation(summary = "重置密码")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/resetPassword/{userId}")
    public Result<?> resetPassword(@Schema(description = "用户ID") @PathVariable Long userId) {
        userService.resetPassword(userId);
        return success();
    }

    @Operation(summary = "获取用户下拉列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/select")
    public Result<List<UserSelectVO>> select(@Valid UserQueryParam param) {
        return success(userService.getSelectUserList(param));
    }
}

