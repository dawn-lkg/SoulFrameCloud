package com.clm.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clm.modules.system.domain.entity.SysJob;
import com.clm.modules.system.domain.param.SysJobParam;
import org.quartz.SchedulerException;

/**
 * 定时任务调度表 服务接口
 */
public interface SysJobService extends IService<SysJob> {


    /**
     * 分页查询定时任务
     *
     * @param param
     * @return
     */
    Page<SysJob> selectJobPage(SysJobParam param);

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    boolean pauseJob(SysJob job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    boolean resumeJob(SysJob job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param jobId 调度信息
     * @return 结果
     */
    void deleteJob(Long jobId) throws SchedulerException;

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     * @return 结果
     */
    void changeStatus(SysJob job) throws SchedulerException;

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void run(SysJob job) throws SchedulerException;

    /**
     * 新增任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void addJob(SysJob job) throws SchedulerException;

    /**
     * 更新任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void updateJob(SysJob job) throws SchedulerException;

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    boolean checkCronExpressionIsValid(String cronExpression);
} 