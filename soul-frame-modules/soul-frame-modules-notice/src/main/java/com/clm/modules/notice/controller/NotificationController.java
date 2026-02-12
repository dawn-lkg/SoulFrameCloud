package com.clm.modules.notice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.enums.BusinessType;
import com.clm.framework.annotation.Log;
import com.clm.system.domain.dto.NotificationDTO;
import com.clm.system.domain.param.NotificationQueryParam;
import com.clm.system.domain.vo.NotificationVO;
import com.clm.system.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@SaCheckLogin
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<IPage<NotificationVO>> list(@Valid NotificationQueryParam param) {
        return success(notificationService.getNotificationPage(param));
    }

    @Operation(summary = "获取未读通知数量")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount() {
        return success(notificationService.getUnreadCount());
    }

    @Operation(summary = "标记通知为已读")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/read/{id}")
    public Result<?> markAsRead(@Parameter(description = "通知ID") @PathVariable Long id) {
        notificationService.markAsRead(id);
        return success();
    }

    @Operation(summary = "标记所有通知为已读")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/read/all")
    public Result<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return success();
    }

    @Operation(summary = "删除通知")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "通知ID") @PathVariable Long id) {
        notificationService.deleteNotification(id);
        return success();
    }

    @Operation(summary = "清空所有通知")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/clear")
    public Result<?> clearAll() {
        notificationService.clearAllNotifications();
        return success();
    }

    @Operation(summary = "新增通知")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public Result<NotificationVO> send(@RequestBody @Valid NotificationDTO notificationVO) {
        return success(notificationService.sendSystemNotification(notificationVO.getUserId(), notificationVO.getTitle(), notificationVO.getContent(), notificationVO.getType(), notificationVO.getColor()));
    }
}
