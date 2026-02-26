package com.clm.api.notice.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息视图对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
public class MessageVO {

    private Long id;

    private String sender;

    private String content;

    private Integer type;

    private Boolean read;

    private String avatar;

    private LocalDateTime time;
}
