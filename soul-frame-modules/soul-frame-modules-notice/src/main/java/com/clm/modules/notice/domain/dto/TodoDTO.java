package com.clm.modules.notice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项数据传输对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@Schema(description = "待办事项数据传输对象")
public class TodoDTO {

    @Schema(description = "待办ID（更新时需要）")
    private Long id;

    @Schema(description = "待办标题")
    @NotBlank(message = "待办标题不能为空")
    @Size(max = 50, message = "待办标题不能超过50个字符")
    private String title;

    @Schema(description = "待办内容")
    @NotBlank(message = "待办内容不能为空")
    @Size(max = 500, message = "待办内容不能超过500个字符")
    private String content;

    @Schema(description = "截止时间")
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;

    @Schema(description = "优先级（high：高，medium：中，low：低）")
    @NotBlank(message = "优先级不能为空")
    private String priority;

    @Schema(description = "状态（pending：待办，completed：已完成，overdue：已逾期，in_progress：进行中，cancelled：已取消）")
    private String status;
}
