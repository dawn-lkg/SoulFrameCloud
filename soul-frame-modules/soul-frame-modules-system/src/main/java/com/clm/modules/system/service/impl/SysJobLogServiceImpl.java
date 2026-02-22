package com.clm.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clm.modules.system.domain.entity.SysJobLog;
import com.clm.modules.system.domain.param.SysJobLogParam;
import com.clm.modules.system.mapper.SysJobLogMapper;
import com.clm.modules.system.service.SysJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 定时任务调度日志信息 服务实现类
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

    @Autowired
    private SysJobLogMapper jobLogMapper;

    @Override
    public void cleanJobLog() {
        jobLogMapper.cleanJobLog();
    }

    @Override
    public Page<SysJobLog> selectPage(SysJobLogParam param) {
        return jobLogMapper.selectPage(new Page<>(param.getPageNum(), param.getPageSize()), query(param));
    }

    @Override
    public void clearJobLog(Long jobId) {
        remove(new LambdaQueryWrapper<SysJobLog>().eq(SysJobLog::getJobId, jobId));
    }

    public LambdaQueryWrapper<SysJobLog> query(SysJobLogParam param) {
        LambdaQueryWrapper<SysJobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getJobName()), SysJobLog::getJobName, param.getJobName());
        queryWrapper.eq(param.getJobId() != null, SysJobLog::getJobId, param.getJobId());
        queryWrapper.eq(param.getStatus() != null, SysJobLog::getStatus, param.getStatus());
        queryWrapper.like(StrUtil.isNotBlank(param.getJobGroup()), SysJobLog::getJobGroup, param.getJobGroup());
        queryWrapper.orderByDesc(SysJobLog::getCreateTime);
        return queryWrapper;
    }
}