package com.clm.api.notice.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
public class NotificationVO {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Boolean read;

    private String color;

    private LocalDateTime time;
}
