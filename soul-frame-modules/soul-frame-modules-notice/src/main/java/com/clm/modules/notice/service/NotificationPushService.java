package com.clm.modules.notice.service;

import com.clm.modules.notice.domain.vo.NotificationVO;

/**
 * 通知推送服务接口
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
public interface NotificationPushService {

    /**
     * 推送系统通知
     *
     * @param userId         用户ID
     * @param notificationVO 通知对象
     */
    void pushNotification(Long userId, NotificationVO notificationVO);

    /**
     * 推送个人消息
     *
     * @param userId    用户ID
     * @param messageVO 消息对象
     */
    void pushMessage(Long userId, Object messageVO);

    /**
     * 推送待办事项
     *
     * @param userId 用户ID
     * @param todoVO 待办事项对象
     */
    void pushTodo(Long userId, Object todoVO);
}
