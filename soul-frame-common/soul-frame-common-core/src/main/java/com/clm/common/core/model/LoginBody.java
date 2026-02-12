package com.clm.common.core.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author 陈黎明
 * @date 2025/3/1 下午9:01
 */


@Data
public class LoginBody {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;

    @NotBlank(message = "uuid不能为空")
    private String uuid;
}
