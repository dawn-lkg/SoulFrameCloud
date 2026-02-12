package com.clm.modules.timer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.quartz.domain.entity.SysJobLog;

/**
 * 定时任务调度日志表 数据层
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 清空任务日志
     */
    void cleanJobLog();
} 