package com.clm.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.clm.api.system.domain.RouterDTO;
import com.clm.auth.domain.entity.UserInfo;
import com.clm.auth.domain.vo.CaptchaVo;
import com.clm.auth.service.AuthService;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.model.LoginBody;
import com.clm.common.core.model.LoginBody2;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 陈黎明
 * @date 2025-03-01
 */

@Tag(name = "鉴权管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody @Valid LoginBody loginBody){
        return success(authService.login(loginBody));
    }

    @Operation(summary = "无验证码登录")
    @PostMapping("/login2")
    public Result<SaTokenInfo> login2(@RequestBody @Valid LoginBody2 loginBody) {
        return success(authService.login2(loginBody));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<?> logout(){
        StpUtil.logout(StpUtil.getLoginId());
        return success();
    }

    @Operation(summary = "获取验证码")
    @GetMapping("captcha")
    public Result<CaptchaVo> getCaptcha(){
        return success(authService.createCaptcha());
    }

    @Operation(summary = "获取用户信息")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo(){
        return success(authService.getUserInfo());
    }

    @Operation(summary = "获取用户菜单权限")
    @Log(businessType = BusinessType.QUERY)
    @GetMapping("/getRouters")
    public Result<List<RouterDTO>> getRouter(){
        return success(authService.getRouter());
    }

}
