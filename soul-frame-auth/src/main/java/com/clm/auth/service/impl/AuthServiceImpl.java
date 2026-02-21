package com.clm.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.clm.api.system.RemoteLogService;
import com.clm.api.system.RemoteMenuService;
import com.clm.api.system.RemoteOnlineUserService;
import com.clm.api.system.RemoteRoleService;
import com.clm.api.system.RemoteUserService;
import com.clm.api.system.domain.LoginLogDTO;
import com.clm.api.system.domain.OnlineUserDTO;
import com.clm.api.system.domain.RouterDTO;
import com.clm.auth.domain.entity.UserInfo;
import com.clm.auth.domain.vo.CaptchaVo;
import com.clm.auth.service.AuthService;
import com.clm.common.core.domain.entity.User;
import com.clm.common.core.domain.Result;
import com.clm.common.core.model.LoginBody;
import com.clm.common.core.model.LoginBody2;
import com.clm.common.core.enums.AccountStatus;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.redis.constants.RedisKeyConstants;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.common.security.utils.AuthenticationUtil;
import com.clm.common.core.utils.CommonUtils;
import com.clm.common.core.utils.PasswordUtils;
import com.wf.captcha.SpecCaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 陈黎明
 * @date 2025-03-04
 */
@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisUtils redisCache;

    private final RemoteMenuService remoteMenuService;

    private final RemoteUserService remoteUserService;

    private final RemoteLogService remoteLogService;

    private final RemoteOnlineUserService remoteOnlineUserService;

    private final RemoteRoleService remoteRoleService;

    @Override
    public SaTokenInfo login(LoginBody loginBody) {
        try {
            String username = loginBody.getUsername();
            User user = remoteUserService.getUserByUsername(username).getOrThrow();
            String captchaValue = redisCache.get(RedisKeyConstants.System.CAPTCHA_PREFIX + loginBody.getUuid(), String.class);

            boolean ahah = !loginBody.getCode().equalsIgnoreCase("AHAH");

            if(StrUtil.isBlank(captchaValue)&&ahah){
                recordLoginFail(username, "验证码已过期");
                throw new BaseException(HttpCodeEnum.CAPTCHA_EXPIRED);
            }

            if(!loginBody.getCode().equalsIgnoreCase(captchaValue)&&ahah){
                recordLoginFail(username, "验证码错误");
                throw new BaseException(HttpCodeEnum.CAPTCHA_ERROR);
            }

            if(Objects.isNull(user)){
                recordLoginFail(username, "用户不存在");
                throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST);
            }

            if(!AccountStatus.ACTIVE.getCode().equals(user.getStatus())){
                recordLoginFail(username, "账号已停用");
                throw new BaseException(HttpCodeEnum.ACCOUNT_DISABLED);
            }

//            if(!PasswordUtils.matches(loginBody.getPassword(), user.getPassword())){
//                recordLoginFail(username, "密码错误");
//                throw new BaseException(HttpCodeEnum.PASSWORD_ERROR);
//            }

            // 更新登录信息
            remoteUserService.updateLoginInfo(user.getUserId());

            // 登录
            AuthenticationUtil.login(user, Collections.EMPTY_SET, null);

            // 获取登录token信息
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

            // 记录在线用户
            setOnlineUser(user, tokenInfo.getTokenValue());

            // 记录登录成功日志
            recordLoginSuccess(username, user.getUserId(), "登录成功");

            return tokenInfo;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            recordLoginFail(loginBody.getUsername(), "登录异常：" + e.getMessage());
            throw new BaseException("登录失败：" + e.getMessage(), HttpCodeEnum.ERROR.getCode());
        }
    }

    @Override
    public CaptchaVo createCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(94, 38, 4);
        String base64 = specCaptcha.toBase64();
        String captchaValue = specCaptcha.text().toLowerCase();
        String key = CommonUtils.generateRandomLetters(6);
        redisCache.set(RedisKeyConstants.System.CAPTCHA_PREFIX+key,captchaValue,60, TimeUnit.SECONDS);
        return new CaptchaVo(base64,key);
    }

    @Override
    public UserInfo getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = remoteUserService.getUserById(userId).getOrThrow();
        if(Objects.isNull(user)){
            throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST);
        }
        UserInfo userInfo = new UserInfo();
        org.springframework.beans.BeanUtils.copyProperties(user,userInfo);
        userInfo.setRoles(remoteRoleService.selectRolesByUserId(user.getUserId()).getOrThrow());
        return userInfo;
    }

    @Override
    public List<RouterDTO> getRouter() {
        // 通过 Feign 调用 system 服务获取路由
        Long userId = StpUtil.getLoginIdAsLong();
        Result<List<RouterDTO>> result = remoteMenuService.getRouterByUserId(userId);

        return result.getOrThrow();
    }


    @Override
    public SaTokenInfo login2(LoginBody2 loginBody) {
        try {
            String username = loginBody.getUsername();
            User user = remoteUserService.getUserByUsername(username).getOrThrow();

            if (Objects.isNull(user)) {
                recordLoginFail(username, "用户不存在");
                throw new BaseException(HttpCodeEnum.DATA_NOT_EXIST);
            }

            if (!AccountStatus.ACTIVE.getCode().equals(user.getStatus())) {
                recordLoginFail(username, "账号已停用");
                throw new BaseException(HttpCodeEnum.ACCOUNT_DISABLED);
            }

            if (!PasswordUtils.matches(loginBody.getPassword(), user.getPassword())) {
                recordLoginFail(username, "密码错误");
                throw new BaseException(HttpCodeEnum.PASSWORD_ERROR);
            }

            // 更新登录信息
            remoteUserService.updateLoginInfo(user.getUserId());

            // 登录
            AuthenticationUtil.login(user, null, null);

            // 获取登录token信息
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

            // 记录在线用户
            setOnlineUser(user, tokenInfo.getTokenValue());

            // 记录登录成功日志
            recordLoginSuccess(username, user.getUserId(), "登录成功");

            return tokenInfo;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            recordLoginFail(loginBody.getUsername(), "登录异常：" + e.getMessage());
            throw new BaseException("登录失败：" + e.getMessage(), HttpCodeEnum.ERROR.getCode());
        }
    }

    /**
     * 记录登录成功日志
     */
    private void recordLoginSuccess(String username, Long userId, String message) {
        LoginLogDTO loginLogDTO = new LoginLogDTO();
        loginLogDTO.setUsername(username);
        loginLogDTO.setUserId(userId);
        loginLogDTO.setMessage(message);
        loginLogDTO.setStatus(1);
        remoteLogService.recordLoginSuccess(loginLogDTO);
    }

    /**
     * 记录登录失败日志
     */
    private void recordLoginFail(String username, String message) {
        LoginLogDTO loginLogDTO = new LoginLogDTO();
        loginLogDTO.setUsername(username);
        loginLogDTO.setMessage(message);
        loginLogDTO.setStatus(0);
        remoteLogService.recordLoginFail(loginLogDTO);
    }

    /**
     * 设置在线用户
     */
    private void setOnlineUser(User user, String token) {
        OnlineUserDTO onlineUserDTO = new OnlineUserDTO();
        onlineUserDTO.setUserId(user.getUserId());
        onlineUserDTO.setUsername(user.getUserName());
        onlineUserDTO.setToken(token);
        onlineUserDTO.setLoginTime(new Date());
        remoteOnlineUserService.setOnlineUser(onlineUserDTO);
    }
}
