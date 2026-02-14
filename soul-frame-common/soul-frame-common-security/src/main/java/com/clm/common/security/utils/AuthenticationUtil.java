package com.clm.common.security.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.clm.common.core.model.LoginUser;
import com.clm.common.core.domain.entity.User;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.security.context.SecurityContextHolder;
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
        LoginUser loginUser = buildLoginUser(user, roles, permissions);

        // 保存登录用户信息到会话中
        StpUtil.login(user.getUserId());

        // 将角色和权限存入 Token 额外数据（供网关透传）
//        StpUtil.getTokenSession().set("roles", roles);
//        StpUtil.getTokenSession().set("permissions", permissions);
//        StpUtil.getTokenSession().set("username", user.getUserName());
//
//        // 保存登录用户信息到 SaToken 的 SessionStorage 中
//        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
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
            // 首先尝试从 SaToken 会话获取
            LoginUser loginUser = (LoginUser) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
            if (Objects.nonNull(loginUser)) {
                return loginUser;
            }

            // 如果 SaToken 会话中没有，尝试从网关透传的 Header 获取（适用于微服务间调用）
            if (SecurityContextHolder.isLoggedIn()) {
                return SecurityContextHolder.getLoginUser();
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
        // 优先从 SaToken 获取
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            return Long.valueOf(loginId.toString());
        }

        // 如果 SaToken 中没有，从网关 Header 获取
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        // 优先从 SaToken 获取
        LoginUser loginUser = getLoginUser();
        if (Objects.nonNull(loginUser) && Objects.nonNull(loginUser.getUsername())) {
            return loginUser.getUsername();
        }

        // 如果 SaToken 中没有，从网关 Header 获取
        return SecurityContextHolder.getUsername();
    }

    /**
     * 检查是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn() {
        return Objects.nonNull(getUserId());
    }

    /**
     * 检查是否拥有指定权限
     *
     * @param permission 权限标识
     * @return true-有权限，false-无权限
     */
    public static boolean hasPermission(String permission) {
        if (!isLoggedIn()) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return false;
        }
        Set<String> permissions = loginUser.getPermissions();
        return Objects.nonNull(permissions) && permissions.contains(permission);
    }

    /**
     * 检查是否拥有指定角色
     *
     * @param role 角色标识
     * @return true-有角色，false-无角色
     */
    public static boolean hasRole(String role) {
        if (!isLoggedIn()) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {
            return false;
        }
        Set<String> roles = loginUser.getRoles();
        return Objects.nonNull(roles) && roles.contains(role);
    }

    /**
     * 构建登录用户对象
     *
     * @param user 用户信息
     * @return 登录用户信息
     */
    private static LoginUser buildLoginUser(User user, Set<String> roles, Set<String> permissions) {
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
} 