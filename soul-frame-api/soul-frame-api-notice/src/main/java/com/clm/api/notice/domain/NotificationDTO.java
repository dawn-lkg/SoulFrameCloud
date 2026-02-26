package com.clm.api.notice.domain;

import lombok.Data;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
public class NotificationDTO {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Long userId;

    private Boolean read;

    private String color;

    public NotificationDTO(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
