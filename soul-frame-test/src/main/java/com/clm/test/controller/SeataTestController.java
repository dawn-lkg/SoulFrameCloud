package com.clm.test.controller;

import com.clm.test.domain.entity.Order;
import com.clm.test.domain.entity.Stock;
import com.clm.test.service.BusinessService;
import io.seata.core.context.RootContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Seata 分布式事务测试 Controller
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class SeataTestController {

    private final BusinessService businessService;

    // ==================== 场景1：本地多数据源事务测试 ====================

    /**
     * 测试1：正常场景 - 分布式事务提交（本地）
     * POST /seata-test/local/order
     */
    @PostMapping("/local/order")
    public Map<String, Object> createLocalOrder(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Order order = businessService.createOrderWithStock(userId, productName, count, amount);

            result.put("success", true);
            result.put("message", "本地分布式事务执行成功");
            result.put("data", order);
        } catch (Exception e) {
            log.error("本地订单创建失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 测试2：异常场景 - 分布式事务回滚（本地）
     * POST /seata-test/local/order/rollback
     */
    @PostMapping("/local/order/rollback")
    public Map<String, Object> createLocalOrderWithRollback(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Order order = businessService.createOrderWithStockRollback(userId, productName, count, amount);

            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", order);
        } catch (Exception e) {
            log.error("本地订单创建失败（预期回滚）", e);
            result.put("success", false);
            result.put("message", "事务已回滚: " + e.getMessage());
        }
        return result;
    }

    /**
     * 测试3：库存不足场景 - 分布式事务回滚（本地）
     * POST /seata-test/local/order/insufficient-stock
     */
    @PostMapping("/local/order/insufficient-stock")
    public Map<String, Object> createLocalOrderInsufficientStock(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Order order = businessService.createOrderWithStock(userId, productName, count, amount);

            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", order);
        } catch (Exception e) {
            log.error("库存不足，事务回滚", e);
            result.put("success", false);
            result.put("message", "事务已回滚: " + e.getMessage());
        }
        return result;
    }

    // ==================== 场景2：跨服务远程调用事务测试 ====================

    /**
     * 测试4：跨服务正常场景 - 分布式事务提交（远程调用）
     * POST /seata-test/remote/order
     */
    @PostMapping("/remote/order")
    public Map<String, Object> createRemoteOrder(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String userName = params.get("userName").toString();
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Map<String, Object> data = businessService.createOrderRemote(userId, userName, productName, count, amount);

            result.put("success", true);
            result.put("message", "跨服务分布式事务执行成功");
            result.put("data", data);
        } catch (Exception e) {
            log.error("跨服务订单创建失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 测试5：跨服务异常场景 - 分布式事务回滚（远程调用）
     * POST /seata-test/remote/order/rollback
     */
    @PostMapping("/remote/order/rollback")
    public Map<String, Object> createRemoteOrderWithRollback(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String userName = params.get("userName").toString();
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Map<String, Object> data = businessService.createOrderRemoteWithRollback(userId, userName, productName, count, amount);

            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", data);
        } catch (Exception e) {
            log.error("跨服务订单创建失败（预期回滚）", e);
            result.put("success", false);
            result.put("message", "事务已回滚: " + e.getMessage());
        }
        return result;
    }

    /**
     * 测试6：跨服务库存不足场景 - 分布式事务回滚（远程调用）
     * POST /seata-test/remote/order/insufficient-stock
     */
    @PostMapping("/remote/order/insufficient-stock")
    public Map<String, Object> createRemoteOrderInsufficientStock(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String userName = params.get("userName").toString();
            String productName = params.get("productName").toString();
            Integer count = Integer.valueOf(params.get("count").toString());
            BigDecimal amount = new BigDecimal(params.get("amount").toString());

            Map<String, Object> data = businessService.createOrderRemoteInsufficientStock(userId, userName, productName, count, amount);

            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("data", data);
        } catch (Exception e) {
            log.error("跨服务库存不足，事务回滚", e);
            result.put("success", false);
            result.put("message", "事务已回滚: " + e.getMessage());
        }
        return result;
    }

    // ==================== 查询接口 ====================

    /**
     * 查询库存
     * GET /seata-test/stock/{productName}
     */
    @GetMapping("/stock/{productName}")
    public Map<String, Object> getStock(@PathVariable String productName) {
        Map<String, Object> result = new HashMap<>();
        try {
            Stock stock = businessService.getStock(productName);
            result.put("success", true);
            result.put("data", stock);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 查询订单
     * GET /seata-test/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public Map<String, Object> getOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Order order = businessService.getOrder(orderId);
            result.put("success", true);
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取当前事务ID
     * GET /seata-test/xid
     */
    @GetMapping("/xid")
    public Map<String, Object> getXid() {
        Map<String, Object> result = new HashMap<>();
        String xid = RootContext.getXID();
        result.put("xid", xid);
        result.put("message", xid != null ? "当前在 Seata 事务中" : "当前不在 Seata 事务中");
        return result;
    }
}
