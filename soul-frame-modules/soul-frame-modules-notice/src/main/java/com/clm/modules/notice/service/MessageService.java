package com.clm.modules.notice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.dto.MessageDTO;
import com.clm.modules.system.domain.entity.Message;
import com.clm.modules.system.domain.param.MessageQueryParam;
import com.clm.modules.system.domain.vo.MessageVO;

/**
 * 消息服务接口
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
public interface MessageService extends IService<Message> {

    /**
     * 获取当前用户的消息分页列表
     *
     * @param param 查询参数
     * @return 消息分页列表
     */
    IPage<MessageVO> getMessagePage(MessageQueryParam param);

    /**
     * 获取当前用户的未读消息数量
     *
     * @return 未读消息数量
     */
    Integer getUnreadCount();

    /**
     * 标记消息为已读
     *
     * @param id 消息ID
     */
    void markAsRead(Long id);

    /**
     * 标记所有消息为已读
     */
    void markAllAsRead();

    /**
     * 删除消息
     *
     * @param id 消息ID
     */
    void deleteMessage(Long id);

    /**
     * 发送消息
     *
     * @param messageDTO 消息数据
     * @return 消息对象
     */
    MessageVO sendMessage(MessageDTO messageDTO);

    /**
     * 获取消息详情
     *
     * @param id 消息ID
     * @return 消息详情
     */
    MessageVO getMessageDetail(Long id);
}
