package com.clm.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.clm.common.core.domain.Result;
import com.clm.common.core.enums.HttpCodeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置类（网关专用）
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Configuration
public class GatewayConfigure {

    /**
     * Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                // 指定拦截路由
                .addInclude("/**")
                // 指定排除路由（白名单）
                .addExclude(
                        "/api/auth/login",
                        "/api/auth/login2",
                        "/api/auth/captcha",
                        "/api/system/config/configGroup",
                        "/api/doc.html",
                        "/api/swagger-ui/**",
                        "/api/swagger-resources/**",
                        "/api/webjars/**",
                        "/api/v3/api-docs/**",
                        "/api/favicon.ico",
                        "/api/actuator/health",
                        "/api/blog/**"
                )
                // 认证函数
                .setAuth(obj -> {
                    StpUtil.checkLogin();
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {

                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()

                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            // .setHeader("X-Frame-Options", "SAMEORIGIN")

                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            // OPTIONS预检请求，不做处理
                            .free(r -> {})
                            .back();
                })
                // 异常处理函数
                .setError(e -> {
//                    if (e instanceof NotLoginException) {
//                        String message = "";
//                        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
//                            message = "未能读取到有效 token";
//                        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
//                            message = "token无效";
//                        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
//                            message = "token已过期";
//                        } else if (e.getType().equals(NotLoginException.KICK_OUT)) {
//                            message = "token已被顶下线";
//                        } else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
//                            message = "token已被顶下线";
//                        } else if (e.getType().equals(NotLoginException.NO_PREFIX)) {
//                            message = "未按照指定前缀提交 token";
//                        } else {
//                            message = "当前会话未登录";
//                        }
//                        return Result.error(HttpCodeEnum.UNAUTHORIZED.getCode(), message);
//                    }
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JSONUtil.toJsonStr(Result.error(HttpCodeEnum.UNAUTHORIZED.getCode(), "当前会话未登录"));
                });
    }
}
