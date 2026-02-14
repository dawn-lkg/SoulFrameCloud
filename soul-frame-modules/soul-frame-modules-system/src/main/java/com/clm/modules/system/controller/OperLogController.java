package com.clm.modules.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.api.system.RemoteLogService;
import com.clm.api.system.domain.OperLogDTO;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.modules.system.domain.entity.OperLog;
import com.clm.modules.system.domain.param.OperLogQueryParam;
import com.clm.modules.system.domain.vo.OperLogVO;
import com.clm.modules.system.domain.vo.VisitingStatisticVO;
import com.clm.modules.system.service.OperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志记录(OperLog)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operLog")
@RequiredArgsConstructor
public class OperLogController extends BaseController {

    private final OperLogService operLogService;

    @Operation(summary = "查询操作日志列表")
    @GetMapping("/page")
    public Result<IPage<OperLogVO>> list(OperLogQueryParam param) {
        IPage<OperLogVO> page = operLogService.pageOperLog(param);
        return success(page);
    }

    @Operation(summary = "获取操作日志详细信息")
    @GetMapping("/{operId}")
    public Result<OperLogVO> getInfo(@Schema(description = "操作日志ID") @PathVariable("operId") Long operId) {
        return success(operLogService.getOperLogById(operId));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{operId}")
    public Result<?> remove(@PathVariable("operId") Long operId) {
        if(!operLogService.removeById(operId)) {
            return error("删除操作日志失败");
        }
        return success();
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    public Result<?> remove(@RequestBody List<Long> operIds) {
        if(!operLogService.removeByIds(operIds)){
            return error("批量删除操作日志失败");
        }
        return success();
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    public Result<?> clean() {
        if (!operLogService.cleanOperLog()) {
           return error("清空操作日志失败");
        }
        return success();
    }



    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    public void export(OperLogQueryParam param) {
        operLogService.exportOperLog(param);
    }

    @Operation(summary = "获取操作访问统计")
    @GetMapping("/visitingStatistic")
    public Result<VisitingStatisticVO> getVisitingStatistic() {
        return success(operLogService.getVisitingStatistic());
    }

    @PostMapping()
    public Result<Boolean> insertOperLog(@RequestBody OperLogDTO operLogDTO) {
        // DTO 转 Entity
        OperLog operLog = new OperLog();
        BeanUtils.copyProperties(operLogDTO, operLog);
        
        // 保存操作日志
        boolean success = operLogService.insertOperLog(operLog);
        return Result.success(success);
    }
} 