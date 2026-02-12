package com.clm.modules.notice.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待办事项查询参数
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "待办事项查询参数")
public class TodoQueryParam extends BasePageParam {

    @Schema(description = "待办标题")
    private String title;

    @Schema(description = "状态（pending：待办，completed：已完成，overdue：已逾期）")
    private String status;

    @Schema(description = "优先级（high：高，medium：中，low：低）")
    private String priority;
}
