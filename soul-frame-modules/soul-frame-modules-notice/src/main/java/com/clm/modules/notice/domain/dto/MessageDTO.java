package com.clm.modules.notice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 消息数据传输对象
 *
 * @author 陈黎明
 * @since 2025-09-10
 */
@Data
@Schema(description = "消息数据传输对象")
public class MessageDTO {

    @Schema(description = "消息ID（更新时需要）")
    private Long id;

    @Schema(description = "接收者用户ID")
    @NotNull(message = "接收者用户ID不能为空")
    private Long receiverId;

    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息内容不能超过500个字符")
    private String content;

    @Schema(description = "消息类型（1：文本，2：图片，3：文件）")
    @NotNull(message = "消息类型不能为空")
    private Integer type;
}
