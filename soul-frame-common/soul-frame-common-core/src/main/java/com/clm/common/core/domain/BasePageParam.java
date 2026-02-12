package com.clm.common.core.domain;

import lombok.Data;

/**
 * 基础分页参数
 *
 * @author 陈黎明
 * @since 2025-03-07
 */
@Data
public class BasePageParam {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
} 