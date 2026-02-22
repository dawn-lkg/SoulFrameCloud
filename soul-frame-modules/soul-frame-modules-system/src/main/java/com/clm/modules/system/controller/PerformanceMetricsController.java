package com.clm.modules.system.controller;

import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.modules.system.domain.param.PerformanceMetricsParam;
import com.clm.modules.system.domain.vo.PerformanceMetricsDayVO;
import com.clm.modules.system.domain.vo.PerformanceMetricsHourVO;
import com.clm.modules.system.domain.vo.PerformanceMetricsRealTimeVO;
import com.clm.modules.system.service.PerformanceMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 性能监控 控制层
 *
 * @author 陈黎明
 * @date 2025-08-20 22:22:24
 */
@RestController
@RequestMapping("/system/performanceMetrics")
@RequiredArgsConstructor
@Validated
@Tag(name = "性能监控管理", description = "性能监控相关接口")
public class PerformanceMetricsController extends BaseController {

    private final PerformanceMetricsService performanceMetricsService;


    @Operation(summary = "实时监控数据")
    @GetMapping("/realtime")
    public Result<PerformanceMetricsRealTimeVO> realtime() {
        return Result.success(performanceMetricsService.realtime());
    }

    @Operation(summary = "按小时获取监控数据")
    @GetMapping("/hour")
    public Result<List<PerformanceMetricsHourVO>> selectPerformanceMetricsHour(PerformanceMetricsParam param) {
        return Result.success(performanceMetricsService.selectPerformanceMetricsHour(param));
    }

    @Operation(summary = "按周获取监控数据")
    @GetMapping("/week")
    public Result<List<PerformanceMetricsDayVO>> selectPerformanceMetricsWeek(PerformanceMetricsParam param) {
        return Result.success(performanceMetricsService.selectPerformanceMetricsWeek(param));
    }
}
