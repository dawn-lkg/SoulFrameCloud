package com.clm.modules.notice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.enums.BusinessType;
import com.clm.framework.annotation.Log;
import com.clm.system.domain.dto.MessageDTO;
import com.clm.system.domain.param.MessageQueryParam;
import com.clm.system.domain.vo.MessageVO;
import com.clm.system.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Tag(name = "消息管理")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@SaCheckLogin
public class MessageController extends BaseController {

    private final MessageService messageService;

    @Operation(summary = "获取消息列表")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/list")
    public Result<IPage<MessageVO>> list(@Valid MessageQueryParam param) {
        return success(messageService.getMessagePage(param));
    }

    @Operation(summary = "获取未读消息数量")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount() {
        return success(messageService.getUnreadCount());
    }

    @Operation(summary = "标记消息为已读")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/read/{id}")
    public Result<?> markAsRead(@Parameter(description = "消息ID") @PathVariable Long id) {
        messageService.markAsRead(id);
        return success();
    }

    @Operation(summary = "标记所有消息为已读")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/read/all")
    public Result<?> markAllAsRead() {
        messageService.markAllAsRead();
        return success();
    }

    @Operation(summary = "删除消息")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "消息ID") @PathVariable Long id) {
        messageService.deleteMessage(id);
        return success();
    }

    @Operation(summary = "发送消息")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public Result<MessageVO> send(@RequestBody @Valid MessageDTO messageDTO) {
        return success(messageService.sendMessage(messageDTO));
    }

    @Operation(summary = "获取消息详情")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/{id}")
    public Result<MessageVO> detail(@Parameter(description = "消息ID") @PathVariable Long id) {
        return success(messageService.getMessageDetail(id));
    }
}
