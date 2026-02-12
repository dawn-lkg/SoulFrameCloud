package com.clm.modules.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.redis.constants.RedisKeyConstants;
import com.clm.common.redis.utils.RedisUtils;
import com.clm.modules.system.domain.dto.MonitorDataDTO;
import com.clm.modules.system.domain.entity.PerformanceMetrics;
import com.clm.modules.system.domain.param.PerformanceMetricsParam;
import com.clm.modules.system.domain.vo.PerformanceMetricsDayVO;
import com.clm.modules.system.domain.vo.PerformanceMetricsHourVO;
import com.clm.modules.system.domain.vo.PerformanceMetricsRealTimeVO;
import com.clm.modules.system.mapper.PerformanceMetricsMapper;
import com.clm.modules.system.service.PerformanceMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 性能监控 服务实现
 *
 * @author 陈黎明
 * @date 2025-08-19 12:30:16
 */
@Service
@RequiredArgsConstructor
public class PerformanceMetricsServiceImpl extends ServiceImpl<PerformanceMetricsMapper, PerformanceMetrics> implements PerformanceMetricsService {

    private final RedisUtils redisUtils;

    @Override
    public PerformanceMetricsRealTimeVO realtime() {
        PerformanceMetricsRealTimeVO performanceMetricsRealTimeVO = new PerformanceMetricsRealTimeVO();
        List<MonitorDataDTO> cpuList = redisUtils.lRange(RedisKeyConstants.Monitor.CPU_USAGE, 0, -1, MonitorDataDTO.class);
        List<MonitorDataDTO> memList = redisUtils.lRange(RedisKeyConstants.Monitor.MEM_USAGE, 0, -1, MonitorDataDTO.class);
        List<String> cpuTimeList = cpuList.stream().map(MonitorDataDTO::getTime).toList();
        List<String> memTimeList = memList.stream().map(MonitorDataDTO::getTime).toList();
        List<String> cpuUsageList = cpuList.stream().map(MonitorDataDTO::getValue).toList();
        List<String> memUsageList = memList.stream().map(MonitorDataDTO::getValue).toList();
        performanceMetricsRealTimeVO.setCpuTimeList(cpuTimeList);
        performanceMetricsRealTimeVO.setCpuUsageList(cpuUsageList);
        performanceMetricsRealTimeVO.setMemTimeList(memTimeList);
        performanceMetricsRealTimeVO.setMemUsageList(memUsageList);
        return performanceMetricsRealTimeVO;
    }

    @Override
    public List<PerformanceMetricsHourVO> selectPerformanceMetricsHour(PerformanceMetricsParam param) {
        return baseMapper.selectPerformanceMetricsHour(param);
    }

    @Override
    public List<PerformanceMetricsDayVO> selectPerformanceMetricsDay(PerformanceMetricsParam param) {
        return baseMapper.selectPerformanceMetricsDay(param);
    }

    @Override
    public List<PerformanceMetricsDayVO> selectPerformanceMetricsWeek(PerformanceMetricsParam param) {
        Date weekStart = DateUtil.beginOfWeek(DateUtil.date());
        Date weekEnd = DateUtil.endOfWeek(DateUtil.date());
        param.setBeginTime(DateUtil.format(weekStart, "yyyy-MM-dd HH:mm:ss"));
        param.setEndTime(DateUtil.format(weekEnd, "yyyy-MM-dd HH:mm:ss"));
        List<PerformanceMetricsDayVO> vos = selectPerformanceMetricsDay(param);
        vos.forEach(vo -> {
            String date = vo.getDate();
            Week week = DateUtil.dayOfWeekEnum(DateUtil.parse(date));
            vo.setDate(week.toChinese());
        });
        return vos;
    }
}
