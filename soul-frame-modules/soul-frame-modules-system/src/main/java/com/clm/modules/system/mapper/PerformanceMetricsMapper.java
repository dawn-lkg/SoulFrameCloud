package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.modules.system.domain.entity.PerformanceMetrics;
import com.clm.modules.system.domain.param.PerformanceMetricsParam;
import com.clm.modules.system.domain.vo.PerformanceMetricsDayVO;
import com.clm.modules.system.domain.vo.PerformanceMetricsHourVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 性能监控 数据层
 *
 * @author 陈黎明
 * @date 2025-08-19 12:30:16
 */
public interface PerformanceMetricsMapper extends BaseMapper<PerformanceMetrics> {

    /**
     * 按小时查询监控数据
     *
     * @return
     */
    List<PerformanceMetricsHourVO> selectPerformanceMetricsHour(@Param("param") PerformanceMetricsParam param);

    /**
     * 按天查询监控数据
     *
     * @return
     */
    List<PerformanceMetricsDayVO> selectPerformanceMetricsDay(@Param("param") PerformanceMetricsParam param);
}
