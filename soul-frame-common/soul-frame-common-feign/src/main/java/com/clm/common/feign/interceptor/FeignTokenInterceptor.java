package com.clm.common.feign.interceptor;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.clm.common.feign.constant.FeignConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign Token 拦截器 - 负责传递 Sa-Token 到下游服务
 * 此拦截器不依赖 Seata，任何服务都可以使用
 */
@Slf4j
@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 标记为 Feign 内部调用
        template.header(FeignConstants.FEIGN_INTERNAL_FLAG, "true");

        // 传递 Token
        try {
            String token = StpUtil.getTokenValue();
            if (StrUtil.isNotBlank(token)) {
                String tokenName = SaManager.getConfig().getTokenName();
                String tokenPrefix = SaManager.getConfig().getTokenPrefix();
                String tokenValue = StrUtil.isNotBlank(tokenPrefix) ? tokenPrefix + " " + token : token;
                template.header(tokenName, tokenValue);
                log.debug("Feign 传递 Token: {}", token);
            }
        } catch (Exception e) {
            // 未登录时忽略，避免报错
            log.trace("Feign Token 传递跳过: 当前无登录信息");
        }
    }
}
