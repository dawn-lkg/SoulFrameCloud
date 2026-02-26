package com.clm.common.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Feign Seata XID 拦截器 - 负责传递 Seata 事务 ID 到下游服务
 * 只有在 Seata 自动配置完成后才会加载，完全无性能损耗
 */
@Slf4j
@Component
@ConditionalOnClass(name = "io.seata.core.context.RootContext")
@ConditionalOnProperty(name = "seata.enable", havingValue = "true", matchIfMissing = false)
public class FeignSeataXidInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 直接调用，无反射开销
        String xid = RootContext.getXID();
        if (xid != null && !xid.isEmpty()) {
            template.header(RootContext.KEY_XID, xid);
            log.info("Feign 传递 Seata XID: {}", xid);
        }
    }
}
