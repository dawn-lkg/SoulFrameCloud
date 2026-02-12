package com.clm.common.security.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.clm.common.core.model.LoginUser;
import com.clm.common.core.domain.entity.User;
import com.clm.common.core.utils.IpUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * 登录助手类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {

    public static final String LOGIN_USER_KEY = "loginUser";
    
    /**
     * 登录系统
     *
     * @param user 用户信息
     */
    public static void login(User user, Set<String> roles, Set<String> permissions) {
        // 构建登录信息
        LoginUser loginUser = buildLoginUser(user,roles, permissions);
        
        // 保存登录用户信息到会话中
        StpUtil.login(user.getUserId());
        
        // 保存登录用户信息到SaToken的SessionStorage中
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }
    
    /**
     * 退出登录
     */
    public static void logout() {
        StpUtil.logout();
    }
    
    /**
     * 获取当前登录用户
     *
     * @return 登录用户信息
     */
    public static LoginUser getLoginUser() {
        try {
            LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
            if (Objects.nonNull(loginUser)) {
                return loginUser;
            }
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        return loginId == null ? null : Long.valueOf(loginId.toString());
    }
    
    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUsername();
    }
    
    /**
     * 构建登录用户对象
     *
     * @param user 用户信息
     * @return 登录用户信息
     */
    private static LoginUser buildLoginUser(User user,Set<String> roles,Set<String> permissions) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setStatus(user.getStatus());
        loginUser.setIpaddr(IpUtils.getIpAddr());
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);
        
        return loginUser;
    }
    
//    /**
//     * 记录登录信息
//     *
//     * @param userId 用户ID
//     */
//    private static void recordLoginInfo(Long userId) {
//        // 获取IP和地址
//        String ip = ServletUtils.getClientIP();
//
//        // 更新用户登录信息的代码可以根据实际需求添加
//        log.info("用户登录成功，用户ID：{}，IP：{}", userId, ip);
//    }
//
//    /**
//     * 是否为超级管理员
//     *
//     * @param userId 用户ID
//     * @return 结果
//     */
//    public static boolean isSuperAdmin(Long userId) {
//        return userId != null && Constants.SUPER_ADMIN_ID.equals(userId);
//    }
    
    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
//    public static boolean isSuperAdmin() {
//        return isSuperAdmin(getUserId());
//    }
} 