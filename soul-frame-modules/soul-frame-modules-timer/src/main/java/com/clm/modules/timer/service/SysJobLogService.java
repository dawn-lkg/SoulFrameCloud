package com.clm.modules.timer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.quartz.domain.entity.SysJobLog;
import com.clm.quartz.domain.param.SysJobLogParam;
import org.springframework.stereotype.Service;

/**
 * 定时任务调度日志表 服务接口
 */
@Service("sysJobLogService")
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 清空任务日志
     */
    void cleanJobLog();

    /**
     * 分页查询
     *
     * @param param 查询参数
     */
    Page<SysJobLog> selectPage(SysJobLogParam param);

    /**
     * 清空任务日志
     *
     * @param jobId 任务id
     */
    void clearJobLog(Long jobId);
}