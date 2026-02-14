package com.clm.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.clm.api.system.domain.RouterDTO;
import com.clm.auth.domain.entity.UserInfo;
import com.clm.auth.domain.vo.CaptchaVo;
import com.clm.common.core.model.LoginBody;
import com.clm.common.core.model.LoginBody2;
import jakarta.validation.Valid;

import java.util.List;

/**
 * @author 陈黎明
 * @date 2025-03-04
 */
public interface AuthService {

    /**
     * 登录
     * @param loginBody 登录信息
     * @return token
     */
    SaTokenInfo login(LoginBody loginBody);

    /**
     * 获取验证码
     * @return 验证码
     */
    CaptchaVo createCaptcha();

    /**
     * 获取用户信息
     * @return 用户信息
     */
    UserInfo getUserInfo();

    /**
     * 获取路由
     * @return 路由
     */
    List<RouterDTO> getRouter();

    /**
     * 无验证码登录
     *
     * @param loginBody 登录信息
     * @return token
     */
    SaTokenInfo login2(@Valid LoginBody2 loginBody);
}
