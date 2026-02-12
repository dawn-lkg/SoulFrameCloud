package com.clm.modules.notice.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息视图对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@Schema(description = "消息视图对象")
public class MessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送者名称")
    private String sender;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型（1：文本，2：图片，3：文件）")
    private Integer type;

    @Schema(description = "是否已读（false：未读，true：已读）")
    private Boolean read;

    @Schema(description = "发送者头像")
    private String avatar;

    @Schema(description = "发送时间")
    private LocalDateTime time;
}
