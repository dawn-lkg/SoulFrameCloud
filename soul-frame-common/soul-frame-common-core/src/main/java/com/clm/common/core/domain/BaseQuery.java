package com.clm.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询基础类
 * 
 * @author 陈黎明
 * @date 2025/3/2
 */
@Data
public class BaseQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页记录数 */
    private Integer pageSize = 10;

    /** 排序字段 */
    private String orderByColumn;

    /** 排序方向 */
    private String isAsc;

    /** 开始时间 */
    private LocalDateTime beginTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
} 