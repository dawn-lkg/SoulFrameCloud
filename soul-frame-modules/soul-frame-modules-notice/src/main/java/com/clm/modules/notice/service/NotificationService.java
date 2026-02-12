package com.clm.modules.notice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.Notification;
import com.clm.modules.system.domain.param.NotificationQueryParam;
import com.clm.modules.system.domain.vo.NotificationVO;

/**
 * 通知服务接口
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 获取当前用户的通知分页列表
     *
     * @param param 查询参数
     * @return 通知分页列表
     */
    IPage<NotificationVO> getNotificationPage(NotificationQueryParam param);

    /**
     * 获取当前用户的未读通知数量
     *
     * @return 未读通知数量
     */
    Integer getUnreadCount();

    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     */
    void markAsRead(Long id);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead();

    /**
     * 删除通知
     *
     * @param id 通知ID
     */
    void deleteNotification(Long id);

    /**
     * 清空所有通知
     */
    void clearAllNotifications();

    /**
     * 发送系统通知
     *
     * @param userId  用户ID
     * @param title   通知标题
     * @param content 通知内容
     * @param type    通知类型
     * @param color   通知颜色
     * @return 通知对象
     */
    NotificationVO sendSystemNotification(Long userId, String title, String content, Integer type, String color);
}
