package com.clm.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.modules.system.domain.entity.SysJob;
import com.clm.modules.system.domain.param.SysJobParam;
import com.clm.modules.system.service.SysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

/**
 * 调度任务信息操作处理
 */
@Tag(name = "定时任务管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/monitor/job")
public class JobController extends BaseController {

    private final SysJobService jobService;

    @Log(businessType = BusinessType.INSERT)
    @Operation(summary = "分页查询定时任务列表")
    @GetMapping("/page")
    public Result<Page<SysJob>> page(SysJobParam param) {
        return Result.success(jobService.selectJobPage(param));
    }


    @Log(businessType = BusinessType.UPDATE)
    @Operation(summary = "获取任务详情")
    @GetMapping(value = "/{jobId}")
    public Result<SysJob> getInfo(@PathVariable("jobId") Long jobId) {
        SysJob byId = jobService.getById(jobId);
        return success(byId);
    }

    @Log(businessType = BusinessType.INSERT)
    @Operation(summary = "新增任务")
    @PostMapping
    public Result<?> add(@RequestBody SysJob job) throws SchedulerException {
        jobService.addJob(job);
        return success();
    }

    @PutMapping
    public Result<?> update(@RequestBody SysJob job) throws SchedulerException {
        jobService.updateJob(job);
        return success();
    }

    @PutMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob one = jobService.getById(job.getJobId());
        if (one == null) {
            return error("任务不存在");
        }
        one.setStatus(job.getStatus());
        jobService.changeStatus(one);
        return success();
    }

    @Operation(summary = "立即执行任务")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public Result<?> run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return success();
    }

    @DeleteMapping("/{jobId}")
    public Result<?> remove(@PathVariable Long jobId) throws SchedulerException {
        jobService.deleteJob(jobId);
        return success();
    }
} 