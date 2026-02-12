package com.clm.modules.notice.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项视图对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@Schema(description = "待办事项视图对象")
public class TodoVO {

    @Schema(description = "待办ID")
    private Long id;

    @Schema(description = "待办标题")
    private String title;

    @Schema(description = "待办内容")
    private String content;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "状态（pending：待办，completed：已完成，overdue：已逾期）")
    private String status;

    @Schema(description = "优先级（high：高，medium：中，low：低）")
    private String priority;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
