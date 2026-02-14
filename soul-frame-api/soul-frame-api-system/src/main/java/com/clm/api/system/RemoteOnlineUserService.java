package com.clm.api.system;

import com.clm.api.system.domain.OnlineUserDTO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 远程在线用户服务 Feign 接口
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@FeignClient(contextId = "RemoteOnlineUserService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteOnlineUserService {

    /**
     * 设置在线用户
     *
     * @param onlineUserDTO 在线用户信息
     * @return 结果
     */
    @PostMapping("/system/online")
    Result<Void> setOnlineUser(@RequestBody OnlineUserDTO onlineUserDTO);
}
