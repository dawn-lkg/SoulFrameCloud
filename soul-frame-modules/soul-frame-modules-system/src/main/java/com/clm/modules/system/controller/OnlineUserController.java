package com.clm.modules.system.controller;

import com.clm.api.system.domain.OnlineUserDTO;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.security.utils.AuthenticationUtil;
import com.clm.common.sse.SseConstant;
import com.clm.common.sse.SseTemplate;
import com.clm.modules.system.domain.entity.OnlineUser;
import com.clm.modules.system.domain.param.OnlineUserQueryParam;
import com.clm.modules.system.service.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 在线用户监控
 *
 * @author 陈黎明
 * @since 2025-03-11
 */

@Tag(name = "在线用户管理")
@RestController
@RequestMapping("/system/online")
@RequiredArgsConstructor
public class OnlineUserController extends BaseController {

    private final OnlineUserService onlineUserService;

    private final SseTemplate sseTemplate;

    @Operation(summary = "获取在线用户列表")
    @GetMapping("/list")
    @Log(businessType = BusinessType.QUERY)
    public Result<List<OnlineUser>> list(OnlineUserQueryParam param) {
        List<OnlineUser> onlineUsers = onlineUserService.listOnlineUsers(param);
        return success(onlineUsers);
    }

    @Operation(summary = "强制退出用户")
    @DeleteMapping("/{userId}")
    @Log(businessType = BusinessType.FORCE)
    public Result<?> forceLogout(@PathVariable String userId) {
        onlineUserService.forceLogout(userId);
        return success();
    }

    @Operation(summary = "批量退出用户")
    @DeleteMapping("batch")
    @Log(businessType = BusinessType.FORCE)
    public Result<?> forceLogout(@RequestBody List<String> userIds) {
        userIds.forEach(onlineUserService::forceLogout);
        return success();
    }

    @Operation(summary = "获取在线用户数量")
    @GetMapping("/count")
    @Log(businessType = BusinessType.QUERY)
    public Result<Long> count() {
        long count = onlineUserService.getOnlineUserCount();
        return success(count);
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        String username = AuthenticationUtil.getUsername();
        return sseTemplate.connect(SseConstant.ONLINE_USER_EVENT, username);
    }

    @Operation(summary = "设置在线用户")
    @PostMapping
    public Result<Void> setOnlineUser(@RequestBody OnlineUserDTO onlineUserDTO) {
        com.clm.common.core.domain.entity.User user = new com.clm.common.core.domain.entity.User();
        user.setUserId(onlineUserDTO.getUserId());
        user.setUserName(onlineUserDTO.getUsername());
        onlineUserService.setOnlineUser(user, onlineUserDTO.getToken());
        return Result.success();
    }
} 