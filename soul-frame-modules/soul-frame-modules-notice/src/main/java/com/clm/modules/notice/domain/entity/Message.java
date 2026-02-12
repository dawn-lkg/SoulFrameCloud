package com.clm.modules.notice.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 个人消息实体类
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_message")
public class Message extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String sender;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型（1：文本，2：图片，3：文件）
     */
    private Integer type;

    /**
     * 是否已读（0：未读，1：已读）
     */
    @TableField("`read`")
    private Boolean read;

    /**
     * 发送者头像
     */
    private String avatar;
}
