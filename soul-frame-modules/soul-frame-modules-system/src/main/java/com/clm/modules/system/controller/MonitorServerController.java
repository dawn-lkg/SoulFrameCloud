package com.clm.modules.system.controller;

import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.server.Cpu;
import com.clm.common.core.domain.server.Mem;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.enums.BusinessType;
import com.clm.modules.system.domain.entity.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author 陈黎明
 * @Date 2025/7/9 23:29
 * @since 2025-03-08
 */
@Tag(name = "服务器管理")
@RestController
@RequestMapping("/monitor/server")
public class MonitorServerController extends BaseController {

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取服务器信息")
    @GetMapping
    public Result<Server> getServerInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return success(server);
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取cpu信息")
    @GetMapping("/cpu")
    public Result<Cpu> getCpuInfo() {
        Server server = new Server();
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        server.setCpuInfo(hardware.getProcessor());
        return success(server.getCpu());
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取内存信息")
    @GetMapping("/mem")
    public Result<Mem> getMemInfo() {
        Server server = new Server();
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        server.setMemInfo(hardware.getMemory());
        return success(server.getMem());
    }

    @Log(businessType = BusinessType.QUERY)
    @Operation(summary = "获取运行时长")
    @GetMapping("/runTime")
    public Result<String> getRunTime() {
        Server server = new Server();
        return success(server.getJvm().getRunHourTime());
    }


}
