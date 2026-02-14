package com.clm.common.security.context;

import com.clm.common.core.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Set;

/**
 * 安全上下文持有者
 * 用于在任意位置获取当前请求的用户信息
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContextHolder {

    /**
     * 用户 ID 请求头名称
     */
    public static final String HEADER_USER_ID = "X-User-Id";

    /**
     * 用户名请求头名称
     */
    public static final String HEADER_USERNAME = "X-Username";

    /**
     * 用户角色请求头名称
     */
    public static final String HEADER_ROLES = "X-User-Roles";

    /**
     * 获取当前请求的 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            throw new IllegalStateException("当前没有请求上下文");
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前请求的 HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            throw new IllegalStateException("当前没有请求上下文");
        }
        return attributes.getResponse();
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        try {
            String userId = getRequest().getHeader(HEADER_USER_ID);
            if (Objects.nonNull(userId) && !userId.isEmpty()) {
                return Long.parseLong(userId);
            }
        } catch (Exception e) {
            log.warn("获取用户ID失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录返回 null
     */
    public static String getUsername() {
        try {
            return getRequest().getHeader(HEADER_USERNAME);
        } catch (Exception e) {
            log.warn("获取用户名失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前登录用户角色列表
     *
     * @return 角色列表字符串，逗号分隔
     */
    public static String getRoles() {
        try {
            return getRequest().getHeader(HEADER_ROLES);
        } catch (Exception e) {
            log.warn("获取角色列表失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 构建 LoginUser 对象
     *
     * @return LoginUser
     */
    public static LoginUser getLoginUser() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(getUserId());
        loginUser.setUsername(getUsername());

        String roles = getRoles();
        if (Objects.nonNull(roles) && !roles.isEmpty()) {
            loginUser.setRoles(Set.of(roles.split(",")));
        }

        return loginUser;
    }

    /**
     * 检查是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLoggedIn() {
        return Objects.nonNull(getUserId());
    }
}
