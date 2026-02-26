package com.clm.test.api;

import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 远程订单服务 Feign 接口（用于演示跨服务调用）
 * 实际项目中应该定义在 api 模块
 */
@FeignClient(
    contextId = "RemoteOrderService",
    value = ServiceNameConstants.SYSTEM_SERVICE,
    fallback = RemoteOrderServiceFallback.class
)
public interface RemoteOrderService {

    /**
     * 远程创建订单
     */
    @PostMapping("/system/order/create")
    Result<Map<String, Object>> createOrder(@RequestBody Map<String, Object> order);

    /**
     * 远程查询订单
     */
    @GetMapping("/system/order/{orderId}")
    Result<Map<String, Object>> getOrder(@PathVariable("orderId") Long orderId);

    /**
     * 远程取消订单
     */
    @PostMapping("/system/order/cancel/{orderId}")
    Result<Void> cancelOrder(@PathVariable("orderId") Long orderId);
}
