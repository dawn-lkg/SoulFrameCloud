package com.clm.api.system;

import com.clm.api.system.domain.OperLogDTO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程日志服务 Feign 接口
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
    @PostMapping("/system/oper-log")
    Result<Boolean> insertOperLog(@RequestBody OperLogDTO operLogDTO);
}
