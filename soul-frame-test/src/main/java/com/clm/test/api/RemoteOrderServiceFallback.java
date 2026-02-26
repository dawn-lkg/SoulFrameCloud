package com.clm.test.api;

import com.clm.common.core.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 远程订单服务降级处理
 */
@Slf4j
@Component
public class RemoteOrderServiceFallback implements RemoteOrderService {

    @Override
    public Result<Map<String, Object>> createOrder(Map<String, Object> order) {
        log.warn("远程调用 createOrder 失败，服务降级");
        return Result.error("远程服务调用失败");
    }

    @Override
    public Result<Map<String, Object>> getOrder(Long orderId) {
        log.warn("远程调用 getOrder 失败，服务降级");
        return Result.error("远程服务调用失败");
    }

    @Override
    public Result<Void> cancelOrder(Long orderId) {
        log.warn("远程调用 cancelOrder 失败，服务降级");
        return Result.error("远程服务调用失败");
    }
}
