package com.clm.api.notice.domain;

import lombok.Data;

/**
 * 消息数据传输对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
public class MessageDTO {

    private Long id;

    private Long receiverId;

    private String content;

    private Integer type;

    public MessageDTO() {
    }

    public MessageDTO(Long receiverId, String content, Integer type) {
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
    }
}
