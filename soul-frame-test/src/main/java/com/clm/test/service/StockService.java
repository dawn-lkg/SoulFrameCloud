package com.clm.test.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clm.test.domain.entity.Stock;
import com.clm.test.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 库存 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS("stock")
public class StockService {

    private final StockMapper stockMapper;

    /**
     * 扣减库存
     */
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(String productName, Integer count) {
        log.info("扣减库存: 商品={}, 数量={}", productName, count);

        // 查询库存
        Stock stock = stockMapper.selectOne(
            new LambdaQueryWrapper<Stock>()
                .eq(Stock::getProductName, productName)
        );

        if (stock == null) {
            throw new RuntimeException("商品不存在: " + productName);
        }

        // 检查库存是否充足
        if (stock.getStock() < count) {
            throw new RuntimeException("库存不足，当前库存: " + stock.getStock() + ", 需要: " + count);
        }

        // 扣减库存
        stock.setStock(stock.getStock() - count);
        int rows = stockMapper.updateById(stock);
        if (rows <= 0) {
            throw new RuntimeException("扣减库存失败");
        }

        log.info("扣减库存成功，剩余库存: {}", stock.getStock());
    }

    /**
     * 查询库存
     */
    public Stock getStockByProductName(String productName) {
        return stockMapper.selectOne(
            new LambdaQueryWrapper<Stock>()
                .eq(Stock::getProductName, productName)
        );
    }

    /**
     * 查询所有库存
     */
    public List<Stock> listStock() {
        return stockMapper.selectList(null);
    }

    /**
     * 初始化库存
     */
    @Transactional(rollbackFor = Exception.class)
    public void initStock(Stock stock) {
        Stock existing = stockMapper.selectOne(
            new LambdaQueryWrapper<Stock>()
                .eq(Stock::getProductName, stock.getProductName())
        );

        if (existing != null) {
            existing.setStock(stock.getStock());
            stockMapper.updateById(existing);
        } else {
            stockMapper.insert(stock);
        }
    }
}
