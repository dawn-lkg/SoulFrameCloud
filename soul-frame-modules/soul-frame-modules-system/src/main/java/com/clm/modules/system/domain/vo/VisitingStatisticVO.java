package com.clm.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
public class VisitingStatisticVO {

    /**
     * 访问范围数据
     */
    List<VisitingRangeDataVO> list;
    /**
     * 今日访问
     */
    @Schema(description = "今日访问")
    private Integer visitsTodayCount;
    /**
     * 昨日访问
     */
    @Schema(description = "昨日访问")
    private Integer visitsYesterdayCount;
    /**
     * 本周访问
     */
    @Schema(description = "本周访问")
    private Integer visitsThisWeekCount;
    /**
     * 上周访问
     */
    @Schema(description = "上周访问")
    private Integer visitsLastWeekCount;
    /**
     * 本月访问
     */
    @Schema(description = "本月访问")
    private Integer visitsThisMonthCount;
    /**
     * 上月访问
     */
    @Schema(description = "上月访问")
    private Integer visitsLastMonthCount;
}
