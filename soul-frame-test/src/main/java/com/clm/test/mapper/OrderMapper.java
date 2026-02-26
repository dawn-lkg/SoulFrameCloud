package com.clm.test.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.test.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper - 订单数据库
 */
@Mapper
@DS("order")
public interface OrderMapper extends BaseMapper<Order> {
}
