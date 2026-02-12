package com.clm.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@Schema(description = "登录参数")
public class LoginBody2 {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}
