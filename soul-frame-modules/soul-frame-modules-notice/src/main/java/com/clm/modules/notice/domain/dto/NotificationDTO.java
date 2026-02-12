package com.clm.modules.notice.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
@Schema(description = "通知DTO")
public class NotificationDTO {

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（1：系统通知，2：安全提醒，3：活动通知）
     */
    private Integer type;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 是否已读（0：未读，1：已读）
     */
    private Boolean read;

    /**
     * 通知颜色（用于前端显示）
     */
    private String color;
}
