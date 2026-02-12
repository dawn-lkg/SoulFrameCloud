package com.clm.modules.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.exception.BaseException;
import com.clm.common.security.utils.AuthenticationUtil;
import com.clm.modules.system.domain.entity.Notification;
import com.clm.modules.system.domain.param.NotificationQueryParam;
import com.clm.modules.system.domain.vo.NotificationVO;
import com.clm.modules.system.mapper.NotificationMapper;
import com.clm.modules.system.service.NotificationPushService;
import com.clm.modules.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 通知服务实现类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final NotificationPushService notificationPushService;

    /**
     * 获取当前用户的通知分页列表
     *
     * @param param 查询参数
     * @return 通知分页列表
     */
    @Override
    public IPage<NotificationVO> getNotificationPage(NotificationQueryParam param) {
        Long userId = LoginHelper.getUserId();
        Page<Notification> page = new Page<>(param.getPageNum(), param.getPageSize());
        return baseMapper.selectNotificationPage(page, userId, param.getTitle(), param.getType(), param.getRead());
    }

    /**
     * 获取当前用户的未读通知数量
     *
     * @return 未读通知数量
     */
    @Override
    public Integer getUnreadCount() {
        Long userId = LoginHelper.getUserId();
        return baseMapper.selectUnreadCount(userId);
    }

    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查通知是否存在且属于当前用户
        Notification notification = getById(id);
        if (notification == null) {
            throw new BaseException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BaseException("无权操作此通知");
        }

        // 如果已经是已读状态，直接返回
        if (Boolean.TRUE.equals(notification.getRead())) {
            return;
        }

        // 更新为已读
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, id)
                .eq(Notification::getUserId, userId)
                .set(Notification::getRead, true)
                .set(Notification::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 标记所有通知为已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = AuthenticationUtil.getUserId();

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getRead, false)
                .eq(Notification::getDelFlag, 0)
                .set(Notification::getRead, true)
                .set(Notification::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 删除通知
     *
     * @param id 通知ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long id) {
        Long userId = LoginHelper.getUserId();

        // 检查通知是否存在且属于当前用户
        Notification notification = getById(id);
        if (notification == null) {
            throw new BaseException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BaseException("无权操作此通知");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getId, id)
                .eq(Notification::getUserId, userId)
                .set(Notification::getDelFlag, 1)
                .set(Notification::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 清空所有通知
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAllNotifications() {
        Long userId = LoginHelper.getUserId();

        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getDelFlag, 0)
                .set(Notification::getDelFlag, 1)
                .set(Notification::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationVO sendSystemNotification(Long userId, String title, String content, Integer type, String color) {
        Notification notification = new Notification();
        notification.setUserId(userId)
                .setTitle(title)
                .setContent(content)
                .setType(type)
                .setColor(color)
                .setRead(false);

        save(notification);

        NotificationVO notificationVO = new NotificationVO();
        BeanUtils.copyProperties(notification, notificationVO);
        notificationVO.setTime(notification.getCreateTime());

        // 推送通知
        notificationPushService.pushNotification(userId, notificationVO);

        return notificationVO;
    }
}
