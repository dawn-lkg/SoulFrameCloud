package com.clm.modules.notice.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@Schema(description = "通知视图对象")
public class NotificationVO {

    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知类型（1：系统通知，2：安全提醒，3：活动通知）")
    private Integer type;

    @Schema(description = "是否已读（false：未读，true：已读）")
    private Boolean read;

    @Schema(description = "通知颜色（用于前端显示）")
    private String color;

    @Schema(description = "通知时间")
    private LocalDateTime time;
}
