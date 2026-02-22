package com.clm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.clm.common.core.utils.StringUtils;
import com.clm.modules.system.domain.entity.SysJob;
import com.clm.modules.system.domain.param.SysJobParam;
import com.clm.modules.system.mapper.SysJobMapper;
import com.clm.modules.system.service.SysJobService;
import com.clm.modules.system.util.CronUtils;
import com.clm.modules.system.constants.ScheduleConstants;
import com.clm.modules.system.util.ScheduleUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务调度信息 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

    private final Scheduler scheduler;

    private final SysJobMapper jobMapper;

    @Value("${timer-task.enable:true}")
    private boolean enable;


    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();
        if (!enable) {
            return;
        }
        List<SysJob> jobList = jobMapper.selectList(null);
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    public Page<SysJob> selectJobPage(SysJobParam param) {
        Page<SysJob> page = jobMapper.selectPage(new Page<>(1, 10), query(param));
        return page;
    }

    public LambdaQueryWrapper<SysJob> query(SysJobParam param) {
        LambdaQueryWrapper<SysJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(param.getJobName()), SysJob::getJobName, param.getJobName());
        queryWrapper.like(StringUtils.isNotBlank(param.getJobGroup()), SysJob::getJobGroup, param.getJobGroup());
        queryWrapper.like(StringUtils.isNotBlank(param.getInvokeTarget()), SysJob::getInvokeTarget, param.getInvokeTarget());
        queryWrapper.eq(StringUtils.isNotBlank(param.getStatus()), SysJob::getStatus, param.getStatus());
        queryWrapper.like(StringUtils.isNotBlank(param.getRemark()), SysJob::getRemark, param.getRemark());
        return queryWrapper;
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.PAUSE);
        boolean b = updateById(job);
        if (b) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return b;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.NORMAL);
        boolean b = updateById(job);
        if (b) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long jobId) throws SchedulerException {
        SysJob job = getById(jobId);
        String jobGroup = job.getJobGroup();
        if (!removeById(jobId)) {
            throw new BaseException("删除任务'" + job.getJobName() + "'失败", HttpCodeEnum.FAILED_DELETE.getCode());
        }
        scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(SysJob job) throws SchedulerException {
        boolean b = false;
        String status = job.getStatus();
        if (ScheduleConstants.NORMAL.equals(status)) {
            b = resumeJob(job);
        } else if (ScheduleConstants.PAUSE.equals(status)) {
            b = pauseJob(job);
        }
        if (!b) {
            throw new BaseException("改变状态'" + job.getJobName() + "'失败", HttpCodeEnum.FAILED_UPDATE.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        SysJob properties = jobMapper.selectById(job.getJobId());
        String jobGroup = properties.getJobGroup();
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 新增任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addJob(SysJob job) throws SchedulerException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            throw new BaseException("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确", HttpCodeEnum.FAILED_ADD.getCode());
        }
        job.setStatus(ScheduleConstants.NORMAL);
        if (!save(job)) {
            throw new BaseException("新增任务失败", HttpCodeEnum.FAILED_ADD.getCode());
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 更新任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(SysJob job) throws SchedulerException {
        if (!CronUtils.isValid(job.getCronExpression())) {
            throw new BaseException("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确", HttpCodeEnum.FAILED_UPDATE.getCode());
        }
        if (!updateById(job)) {
            throw new BaseException("修改任务失败", HttpCodeEnum.FAILED_UPDATE.getCode());
        }
        updateSchedulerJob(job, job.getJobGroup());
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }
} 