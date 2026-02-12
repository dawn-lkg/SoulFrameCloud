package com.clm.modules.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.modules.system.domain.entity.Message;
import com.clm.modules.system.domain.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息数据库访问层
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取当前用户的消息分页列表
     *
     * @param page       分页参数
     * @param receiverId 接收者用户ID
     * @param sender     发送者名称
     * @param content    消息内容
     * @param type       消息类型
     * @param read       是否已读
     * @return 消息分页列表
     */
    IPage<MessageVO> selectMessagePage(Page<Message> page,
                                       @Param("receiverId") Long receiverId,
                                       @Param("sender") String sender,
                                       @Param("content") String content,
                                       @Param("type") Integer type,
                                       @Param("read") Boolean read);

    /**
     * 获取当前用户的未读消息数量
     *
     * @param receiverId 接收者用户ID
     * @return 未读消息数量
     */
    Integer selectUnreadCount(@Param("receiverId") Long receiverId);
}
