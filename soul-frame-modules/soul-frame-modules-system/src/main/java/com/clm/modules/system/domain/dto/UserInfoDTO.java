package com.clm.modules.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
public class UserInfoDTO {
    /**
     * 用户ID（编辑时必填）
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;
}
