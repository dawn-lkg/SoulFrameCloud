package com.clm.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈黎明
 * @date 2025/3/4 上午9:20
 */

@Data
public class BaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 关键字搜索
     */
    private String keyword;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
