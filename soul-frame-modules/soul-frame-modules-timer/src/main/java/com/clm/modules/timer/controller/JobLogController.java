package com.clm.modules.timer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.enums.BusinessType;
import com.clm.framework.annotation.Log;
import com.clm.quartz.domain.entity.SysJobLog;
import com.clm.quartz.domain.param.SysJobLogParam;
import com.clm.quartz.service.SysJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度日志操作处理
 */
@Tag(name = "定时任务调度日志")
@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/jobLog")
public class JobLogController extends BaseController {

    @Resource
    private SysJobLogService jobLogService;

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "分页查询定时任务调度日志")
    @GetMapping("/page")
    public Result<Page<SysJobLog>> page(SysJobLogParam param) {
        Page<SysJobLog> page = jobLogService.selectPage(param);
        return success(page);
    }

    @Log(businessType = BusinessType.CLEAN)
    @Operation(summary = "清空定时任务调度日志")
    @DeleteMapping("/clean")
    public Result<?> clean(Long jobId) {
        jobLogService.clearJobLog(jobId);
        return success();
    }

} 