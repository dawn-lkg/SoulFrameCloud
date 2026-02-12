package com.clm.modules.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.system.domain.entity.Notification;
import com.clm.modules.system.domain.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知数据库访问层
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 获取当前用户的通知分页列表
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param title  通知标题
     * @param type   通知类型
     * @param read   是否已读
     * @return 通知分页列表
     */
    IPage<NotificationVO> selectNotificationPage(Page<Notification> page,
                                                 @Param("userId") Long userId,
                                                 @Param("title") String title,
                                                 @Param("type") Integer type,
                                                 @Param("read") Boolean read);

    /**
     * 获取当前用户的未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer selectUnreadCount(@Param("userId") Long userId);
}
