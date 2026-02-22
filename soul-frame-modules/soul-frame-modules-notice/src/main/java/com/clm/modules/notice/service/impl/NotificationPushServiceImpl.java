package com.clm.modules.notice.service.impl;

import com.clm.common.sse.SseConstant;
import com.clm.common.sse.SseTemplate;
import com.clm.modules.notice.domain.vo.NotificationVO;
import com.clm.modules.notice.service.NotificationPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知推送服务实现类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPushServiceImpl implements NotificationPushService {

    private final SseTemplate sseTemplate;

    /**
     * 推送系统通知
     *
     * @param userId         用户ID
     * @param notificationVO 通知对象
     */
    @Override
    public void pushNotification(Long userId, NotificationVO notificationVO) {
        sseTemplate.send(SseConstant.MESSAGE_TODO_NOTICE_EVENT, userId.toString(), SseConstant.NOTIFICATION_EVENT, notificationVO);
    }

    /**
     * 推送个人消息
     *
     * @param userId    用户ID
     * @param messageVO 消息对象
     */
    @Override
    public void pushMessage(Long userId, Object messageVO) {
        sseTemplate.send(SseConstant.MESSAGE_TODO_NOTICE_EVENT, userId.toString(), SseConstant.MESSAGE_EVENT, messageVO);
    }

    /**
     * 推送待办事项
     *
     * @param userId 用户ID
     * @param todoVO 待办事项对象
     */
    @Override
    public void pushTodo(Long userId, Object todoVO) {
        sseTemplate.send(SseConstant.MESSAGE_TODO_NOTICE_EVENT, userId.toString(), SseConstant.TODO_EVENT, todoVO);
    }


}
