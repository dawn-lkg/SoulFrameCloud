package com.clm.test.service;

import com.clm.common.core.domain.Result;
import com.clm.test.api.RemoteOrderService;
import com.clm.test.domain.entity.Order;
import com.clm.test.domain.entity.Stock;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务服务 - Seata 分布式事务测试
 * 模拟场景1：本地多数据源事务（订单库 + 库存库）
 * 模拟场景2：跨服务远程调用事务（soul-frame-test 调用 soul-frame-modules-system）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessService {

    private final OrderService orderService;
    private final StockService stockService;
    private final RemoteOrderService remoteOrderService;

    // ==================== 场景1：本地多数据源事务 ====================

    /**
     * Seata 分布式事务测试 - 正常场景
     * 订单创建成功 + 库存扣减成功 = 事务提交
     */
    @GlobalTransactional(name = "create-order-stock", timeoutMills = 3000, rollbackFor = Exception.class)
    public Order createOrderWithStock(Long userId, String productName, Integer count, BigDecimal amount) {
        log.info("========== [本地事务] 开始分布式事务 ==========");
        log.info("用户: {}, 商品: {}, 数量: {}, 金额: {}", userId, productName, count, amount);

        // 1. 扣减库存（操作 stock_db）
        log.info("Step 1: 扣减库存");
        stockService.deductStock(productName, count);

        // 2. 创建订单（操作 order_db）
        log.info("Step 2: 创建订单");
        Order order = orderService.createOrder(userId, productName, count, amount);

        log.info("========== [本地事务] 分布式事务执行成功 ==========");
        return order;
    }

    /**
     * Seata 分布式事务测试 - 异常场景（库存不足）
     */
    @GlobalTransactional(name = "create-order-stock-rollback", rollbackFor = Exception.class)
    public Order createOrderWithStockRollback(Long userId, String productName, Integer count, BigDecimal amount) {
        log.info("========== [本地事务] 开始分布式事务（测试回滚）==========");
        log.info("用户: {}, 商品: {}, 数量: {}, 金额: {}", userId, productName, count, amount);

        // 1. 扣减库存（操作 stock_db）
        log.info("Step 1: 扣减库存");
        stockService.deductStock(productName, count);

        // 2. 创建订单（操作 order_db）
        log.info("Step 2: 创建订单");
        Order order = orderService.createOrder(userId, productName, count, amount);

        // 3. 模拟异常，触发回滚
        log.info("Step 3: 模拟异常，触发回滚");
        throw new RuntimeException("模拟业务异常，触发分布式事务回滚！");
    }

    // ==================== 场景2：跨服务远程调用事务 ====================

    /**
     * 跨服务分布式事务 - 正常场景
     * 远程调用 system 服务创建订单 + 本地扣减库存 = 事务提交
     */
    @GlobalTransactional(name = "remote-create-order-stock", timeoutMills = 10000, rollbackFor = Exception.class)
    public Map<String, Object> createOrderRemote(Long userId, String userName, String productName, Integer count, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        log.info("========== [远程调用] 开始跨服务分布式事务 ==========");
        log.info("用户ID: {}, 用户名: {}, 商品: {}, 数量: {}, 金额: {}", userId, userName, productName, count, amount);

        // 1. 远程调用 system 服务创建订单（跨服务调用）
        log.info("Step 1: 远程调用 system 服务创建订单");
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("userName", userName);
        orderData.put("productName", productName);
        orderData.put("count", count);
        orderData.put("amount", amount);

        Result<Map<String, Object>> remoteResult = remoteOrderService.createOrder(orderData);
        if (remoteResult == null || !remoteResult.isSuccess()) {
            throw new RuntimeException("远程调用创建订单失败: " + (remoteResult != null ? remoteResult.getMsg() : "无响应"));
        }

        log.info("远程订单创建成功: {}", remoteResult.getData());
        result.put("remoteOrder", remoteResult.getData());

        // 2. 本地扣减库存（本地数据库）
        log.info("Step 2: 本地扣减库存");
        stockService.deductStock(productName, count);

        log.info("========== [远程调用] 跨服务分布式事务执行成功 ==========");
        return result;
    }

    /**
     * 跨服务分布式事务 - 异常场景（远程服务失败触发回滚）
     */
    @GlobalTransactional(name = "remote-create-order-stock-rollback", timeoutMills = 10000, rollbackFor = Exception.class)
    public Map<String, Object> createOrderRemoteWithRollback(Long userId, String userName, String productName, Integer count, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        log.info("========== [远程调用] 开始跨服务分布式事务（测试回滚）==========");
        log.info("用户ID: {}, 用户名: {}, 商品: {}, 数量: {}, 金额: {}", userId, userName, productName, count, amount);

        // 1. 远程调用 system 服务创建订单（跨服务调用）
        log.info("Step 1: 远程调用 system 服务创建订单");
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("userName", userName);
        orderData.put("productName", productName);
        orderData.put("count", count);
        orderData.put("amount", amount);

        Result<Map<String, Object>> remoteResult = remoteOrderService.createOrder(orderData);
        if (remoteResult == null || !remoteResult.isSuccess()) {
            throw new RuntimeException("远程调用创建订单失败: " + (remoteResult != null ? remoteResult.getMsg() : "无响应"));
        }

        log.info("远程订单创建成功: {}", remoteResult.getData());
        result.put("remoteOrder", remoteResult.getData());

        // 2. 本地扣减库存（本地数据库）
        log.info("Step 2: 本地扣减库存");
        stockService.deductStock(productName, count);

        // 3. 模拟异常，触发回滚
        log.info("Step 3: 模拟异常，触发回滚");
        throw new RuntimeException("模拟业务异常，触发跨服务分布式事务回滚！");
    }

    /**
     * 跨服务分布式事务 - 库存不足场景（本地服务失败触发回滚）
     */
    @GlobalTransactional(name = "remote-insufficient-stock", timeoutMills = 10000, rollbackFor = Exception.class)
    public Map<String, Object> createOrderRemoteInsufficientStock(Long userId, String userName, String productName, Integer count, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        log.info("========== [远程调用] 开始跨服务分布式事务（库存不足测试）==========");
        log.info("用户ID: {}, 用户名: {}, 商品: {}, 数量: {}, 金额: {}", userId, userName, productName, count, amount);

        // 1. 远程调用 system 服务创建订单（跨服务调用）
        log.info("Step 1: 远程调用 system 服务创建订单");
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("userName", userName);
        orderData.put("productName", productName);
        orderData.put("count", count);
        orderData.put("amount", amount);

        Result<Map<String, Object>> remoteResult = remoteOrderService.createOrder(orderData);
        if (remoteResult == null || !remoteResult.isSuccess()) {
            throw new RuntimeException("远程调用创建订单失败: " + (remoteResult != null ? remoteResult.getMsg() : "无响应"));
        }

        log.info("远程订单创建成功: {}", remoteResult.getData());
        result.put("remoteOrder", remoteResult.getData());

        // 2. 本地扣减库存（本地数据库）- 库存不足会抛异常
        log.info("Step 2: 本地扣减库存（库存不足）");
        stockService.deductStock(productName, count);

        log.info("========== [远程调用] 跨服务分布式事务完成 ==========");
        return result;
    }

    // ==================== 查询方法 ====================

    public Stock getStock(String productName) {
        return stockService.getStockByProductName(productName);
    }

    public Order getOrder(Long orderId) {
        return orderService.getOrderById(orderId);
    }
}
