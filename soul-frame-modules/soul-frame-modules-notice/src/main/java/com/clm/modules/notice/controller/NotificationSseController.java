package com.clm.modules.notice.controller;

import com.clm.common.core.controller.BaseController;
import com.clm.common.security.LoginHelper;
import com.clm.sse.SseConstant;
import com.clm.sse.SseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 消息通知SSE控制器
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Tag(name = "消息通知SSE")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationSseController extends BaseController {

    private final SseTemplate sseTemplate;

    /**
     * 建立SSE连接
     */
    @Operation(summary = "建立SSE连接")
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        Long userId = LoginHelper.getUserId();
        String username = LoginHelper.getUsername();
        log.info("用户[{}]建立SSE连接", username);
        // 创建SSE连接，使用用户ID作为客户端标识
        SseEmitter emitter = sseTemplate.connect(SseConstant.MESSAGE_TODO_NOTICE_EVENT, userId.toString());
        return emitter;
    }
}
