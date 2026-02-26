package com.clm.test.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.test.domain.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存 Mapper - 库存数据库
 */
@Mapper
@DS("stock")
public interface StockMapper extends BaseMapper<Stock> {
}
