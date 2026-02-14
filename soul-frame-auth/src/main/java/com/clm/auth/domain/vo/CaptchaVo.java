package com.clm.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenliming
 * @date 2024/5/26 上午12:58
 */

@Data
public class CaptchaVo {

    @Schema(description = "验证码图片Base64")
    private String captchaSvg;

    @Schema(description = "验证码key")
    private String captchaCode;

    public CaptchaVo(){}

    public CaptchaVo(String captchaSvg, String captchaCode) {
        this.captchaSvg = captchaSvg;
        this.captchaCode = captchaCode;
    }
}
