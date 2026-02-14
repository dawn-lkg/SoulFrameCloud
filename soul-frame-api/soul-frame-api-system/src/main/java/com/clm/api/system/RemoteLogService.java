package com.clm.api.system;

import com.clm.api.system.domain.LoginLogDTO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程登录日志服务 Feign 接口
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@FeignClient(contextId = "RemoteLogService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteLogService {

    /**
     * 保存操作日志
     *
     * @param operLogDTO 操作日志 DTO
     * @return 结果
     */
    @PostMapping("/system/operLog")
    Result<Boolean> insertOperLog(@RequestBody com.clm.api.system.domain.OperLogDTO operLogDTO);

    /**
     * 记录登录成功日志
     *
     * @param loginLogDTO 登录日志 DTO
     * @return 结果
     */
    @PostMapping("/system/loginLog/success")
    Result<Void> recordLoginSuccess(@RequestBody LoginLogDTO loginLogDTO);

    /**
     * 记录登录失败日志
     *
     * @param loginLogDTO 登录日志 DTO
     * @return 结果
     */
    @PostMapping("/system/loginLog/fail")
    Result<Void> recordLoginFail(@RequestBody LoginLogDTO loginLogDTO);
}
