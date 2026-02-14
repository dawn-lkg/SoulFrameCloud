package com.clm.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.BasePageParam;
import com.clm.common.core.domain.Result;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.modules.system.domain.entity.LoginLog;
import com.clm.modules.system.domain.param.LoginLogQueryParam;
import com.clm.modules.system.domain.vo.LoginLogVO;
import com.clm.modules.system.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统访问记录(LoginLog)表控制层
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/system/loginLog")
@RequiredArgsConstructor
public class LoginLogController extends BaseController {

    private final LoginLogService loginLogService;

    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    @Log(businessType = BusinessType.QUERY)
    public Result<IPage<LoginLogVO>> page(LoginLogQueryParam param) {
        return success(loginLogService.pageLoginLog(param));
    }

    @Operation(summary = "获取登录日志详情")
    @GetMapping("/{infoId}")
    @Log(businessType = BusinessType.QUERY)
    public Result<LoginLogVO> info(@PathVariable("infoId") Long infoId) {
        return success(loginLogService.getLoginLogById(infoId));
    }

    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{infoId}")
    @Log(businessType = BusinessType.DELETE)
    public Result<?> delete(@PathVariable("infoId") Long infoId) {
        loginLogService.removeById(infoId);
        return success();
    }

    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/batch")
    @Log(businessType = BusinessType.DELETE)
    public Result<?> delete(@RequestBody List<Long> infoIds) {
        loginLogService.deleteLoginLogByIds(infoIds);
        return success();
    }

    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clean")
    @Log(businessType = BusinessType.CLEAN)
    public Result<?> clean() {
        loginLogService.cleanLoginLog();
        return success();
    }

    @Operation(summary = "导出登录日志")
    @GetMapping("/export")
    @Log(businessType = BusinessType.EXPORT)
    public void export(LoginLogQueryParam param) {
        loginLogService.exportLoginLog(param);
    }


    @Operation(summary = "获取当前用户的登录列表")
    @GetMapping("/current")
    @Log(businessType = BusinessType.QUERY)
    public Result<IPage<LoginLogVO>> current(BasePageParam param) {
        return success(loginLogService.getCurrentUserLoginLog(param));
    }

    @Operation(summary = "记录登录成功日志")
    @PostMapping("/success")
    public Result<Void> recordLoginSuccess(@RequestBody LoginLog loginLog) {
        loginLogService.recordLoginSuccess(loginLog.getUserName(), loginLog.getUserId(), loginLog.getMsg());
        return Result.success();
    }

    @Operation(summary = "记录登录失败日志")
    @PostMapping("/fail")
    public Result<Void> recordLoginFail(@RequestBody LoginLog loginLog) {
        loginLogService.recordLoginFail(loginLog.getUserName(), loginLog.getMsg());
        return Result.success();
    }
} 