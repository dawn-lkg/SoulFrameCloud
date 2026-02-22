package com.clm.modules.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.exception.BaseException;
import com.clm.common.core.model.LoginUser;
import com.clm.common.sse.SseConstant;
import com.clm.common.sse.SseTemplate;
import com.clm.modules.notice.domain.dto.MessageDTO;
import com.clm.modules.notice.domain.entity.Message;
import com.clm.modules.notice.domain.param.MessageQueryParam;
import com.clm.modules.notice.domain.vo.MessageVO;
import com.clm.modules.notice.mapper.MessageMapper;
import com.clm.modules.notice.service.MessageService;
import com.clm.modules.notice.service.NotificationPushService;
import com.clm.common.security.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 消息服务实现类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final NotificationPushService notificationPushService;
    private final SseTemplate sseTemplate;

    @Override
    public IPage<MessageVO> getMessagePage(MessageQueryParam param) {
        Long userId = AuthenticationUtil.getUserId();
        Page<Message> page = new Page<>(param.getPageNum(), param.getPageSize());
        return baseMapper.selectMessagePage(page, userId, param.getSender(), param.getContent(), param.getType(), param.getRead());
    }

    @Override
    public Integer getUnreadCount() {
        Long userId = AuthenticationUtil.getUserId();
        return baseMapper.selectUnreadCount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id) {
        Long userId = AuthenticationUtil.getUserId();

        // 检查消息是否存在且属于当前用户
        Message message = getById(id);
        if (Objects.isNull(message)) {
            throw new BaseException("消息不存在");
        }
        if (!message.getReceiverId().equals(userId)) {
            throw new BaseException("无权操作此消息");
        }

        // 如果已经是已读状态，直接返回
        if (Boolean.TRUE.equals(message.getRead())) {
            return;
        }

        // 更新为已读
        lambdaUpdate().eq(Message::getId, id)
                .eq(Message::getReceiverId, userId)
                .set(Message::getRead, true)
                .set(Message::getUpdateTime, LocalDateTime.now())
                .update();

        // 推送已读消息
        sseTemplate.send(SseConstant.MESSAGE_TODO_NOTICE_EVENT, userId.toString(), SseConstant.MESSAGE_EVENT, "");
    }

    /**
     * 标记所有消息为已读
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        Long userId = AuthenticationUtil.getUserId();

        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getRead, false)
                .eq(Message::getDelFlag, 0)
                .set(Message::getRead, true)
                .set(Message::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 删除消息
     *
     * @param id 消息ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long id) {
        Long userId = AuthenticationUtil.getUserId();

        // 检查消息是否存在且属于当前用户
        Message message = getById(id);
        if (message == null) {
            throw new BaseException("消息不存在");
        }
        if (!message.getReceiverId().equals(userId)) {
            throw new BaseException("无权操作此消息");
        }

        // 逻辑删除
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getId, id)
                .eq(Message::getReceiverId, userId)
                .set(Message::getDelFlag, 1)
                .set(Message::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    /**
     * 发送消息
     *
     * @param messageDTO 消息数据
     * @return 消息对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO sendMessage(MessageDTO messageDTO) {
        Long userId = AuthenticationUtil.getUserId();
        LoginUser loginUser = AuthenticationUtil.getLoginUser();
        if(loginUser == null){
            throw new BaseException("用户不存在");
        }

        Message message = new Message();
        message.setSenderId(userId)
                .setSender(loginUser.getUsername())
                .setReceiverId(messageDTO.getReceiverId())
                .setContent(messageDTO.getContent())
                .setType(messageDTO.getType())
                .setRead(false)
                .setAvatar(loginUser.getAvatar());

        save(message);

        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);
        messageVO.setTime(message.getCreateTime());

        // 推送消息
        notificationPushService.pushMessage(messageDTO.getReceiverId(), messageVO);

        return messageVO;
    }

    /**
     * 获取消息详情
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @Override
    public MessageVO getMessageDetail(Long id) {
        Long userId = AuthenticationUtil.getUserId();

        // 检查消息是否存在且属于当前用户
        Message message = getById(id);
        if (message == null) {
            throw new BaseException("消息不存在");
        }
        if (!message.getReceiverId().equals(userId)) {
            throw new BaseException("无权查看此消息");
        }

        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);
        messageVO.setTime(message.getCreateTime());

        // 如果未读，标记为已读
        if (Boolean.FALSE.equals(message.getRead())) {
            markAsRead(id);
        }

        return messageVO;
    }
}
