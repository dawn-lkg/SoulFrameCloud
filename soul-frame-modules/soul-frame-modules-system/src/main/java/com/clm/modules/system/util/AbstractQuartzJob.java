package com.clm.modules.system.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.clm.modules.system.constants.ScheduleConstants;
import com.clm.modules.system.domain.entity.SysJob;
import com.clm.modules.system.domain.entity.SysJobLog;
import com.clm.modules.system.service.SysJobLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象Quartz调用
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJob sysJob = new SysJob();
        BeanUtil.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), sysJob);
        try {
            before(context, sysJob);
            if (sysJob != null) {
//                log.info("任务准备执行  - ：{}", sysJob.getJobName());
                doExecute(context, sysJob);
//                log.info("任务执行完毕  - ：{} 总共耗时：{} 毫秒", sysJob.getJobName(), System.currentTimeMillis() - threadLocal.get().getTime());
            }
            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        try {
            Date startTime = threadLocal.get();
            threadLocal.remove();

            if (startTime == null) {
                return;
            }

            final SysJobLog sysJobLog = new SysJobLog();
            sysJobLog.setJobName(sysJob.getJobName());
            sysJobLog.setJobGroup(sysJob.getJobGroup());
            sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
            sysJobLog.setJobId(sysJob.getJobId());
            sysJobLog.setStartTime(startTime);
            sysJobLog.setStopTime(new Date());
            sysJobLog.setCreateTime(new Date());
            long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
            sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
            if (e != null) {
                sysJobLog.setStatus("1");
                String errorMsg = ExceptionUtil.getMessage(e);
                sysJobLog.setExceptionInfo(errorMsg);
            } else {
                sysJobLog.setStatus("0");
            }

            // 获取Spring上下文
            SysJobLogService jobLogService = SpringUtils.getBean(SysJobLogService.class);
            if (jobLogService != null) {
                // 写入数据库当中
                jobLogService.save(sysJobLog);
            } else {
                log.error("获取SysJobLogService失败，无法保存任务日志");
            }
        } catch (Exception ex) {
            log.error("保存任务日志时发生异常", ex);
        }
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
} 