package com.clm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clm.modules.system.domain.entity.SysJobLog;

/**
 * 定时任务调度日志表 数据层
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    /**
     * 清空任务日志
     */
    void cleanJobLog();
} 