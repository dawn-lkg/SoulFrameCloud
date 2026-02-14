package com.clm.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;

/**
 * 网关鉴权过滤器
 * 职责：
 * 1. 解析并校验 Token
 * 2. 将用户信息透传至下游服务
 * 3. 记录访问日志
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

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
     * Token 请求头名称
     */
    public static final String HEADER_TOKEN = "Authorization";

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 获取请求路径
        String path = request.getPath().value();
        log.debug("请求路径: {}", path);

        // 如果未登录，直接放行（由 SaReactorFilter 处理）
        // 这里主要负责用户信息透传
        try {
            // 检查是否已登录
            if (StpUtil.isLogin()) {
                // 获取登录用户信息
                Object loginId = StpUtil.getLoginIdDefaultNull();
                if (loginId != null) {
                    // 构建带用户信息的请求头
                    ServerHttpRequest newRequest = request.mutate()
                            .header(HEADER_USER_ID, String.valueOf(loginId))
                            .header(HEADER_USERNAME, StpUtil.getExtra("username") != null ?
                                    String.valueOf(StpUtil.getExtra("username")) : "")
                            .header(HEADER_ROLES, getRolesFromLogin())
                            .build();

                    exchange = exchange.mutate().request(newRequest).build();
                    log.debug("用户 {} 已登录，用户ID: {}", loginId, loginId);
                }
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败: {}", e.getMessage());
        }

        return chain.filter(exchange);
    }

    /**
     * 从登录信息中获取角色列表
     */
    @SuppressWarnings("unchecked")
    private String getRolesFromLogin() {
        try {
            // 从 Token 中获取角色信息
            Object roles = StpUtil.getExtra("roles");
            if (roles instanceof Collection) {
                return String.join(",", (Collection<String>) roles);
            }
        } catch (Exception e) {
            log.debug("获取角色信息失败: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public int getOrder() {
        // 放在最前面执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
