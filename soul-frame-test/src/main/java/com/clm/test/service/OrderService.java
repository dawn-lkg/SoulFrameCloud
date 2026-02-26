package com.clm.test.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clm.test.domain.entity.Order;
import com.clm.test.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS("order")
public class OrderService {

    private final OrderMapper orderMapper;

    /**
     * 创建订单（本地事务，仅操作订单库）
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, String productName, Integer count, BigDecimal amount) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductName(productName);
        order.setCount(count);
        order.setAmount(amount);
        order.setStatus(0); // 创建状态
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("订单创建成功: {}", order.getOrderNo());

        return order;
    }

    /**
     * 查询订单
     */
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    /**
     * 根据订单号查询订单
     */
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOne(
            new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
}
