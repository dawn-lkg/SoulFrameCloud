package com.clm.modules.notice.domain.param;

import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息查询参数
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息查询参数")
public class MessageQueryParam extends BasePageParam {

    @Schema(description = "发送者名称")
    private String sender;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型（1：文本，2：图片，3：文件）")
    private Integer type;

    @Schema(description = "是否已读（0：未读，1：已读）")
    private Boolean read;
}
