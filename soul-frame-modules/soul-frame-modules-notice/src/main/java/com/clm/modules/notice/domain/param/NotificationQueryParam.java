package com.clm.modules.notice.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知查询参数
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知查询参数")
public class NotificationQueryParam extends BasePageParam {

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知类型（1：系统通知，2：安全提醒，3：活动通知）")
    private Integer type;

    @Schema(description = "是否已读（0：未读，1：已读）")
    private Boolean read;
}
