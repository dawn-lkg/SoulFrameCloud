package com.clm.modules.system.domain.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitingStatisticParam {
    @Schema(description = "开始时间")
    private String startTime;
    @Schema(description = "结束时间")
    private String endTime;
    @Schema(description = "类型 1.今日 2.昨日 3.本周 4.上周 5.本月 6.上月")
    private Integer type;

    public VisitingStatisticParam(Integer type) {
        this.type = type;
    }

    public VisitingStatisticParam(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
