package com.clm.api.system;

import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 远程用户服务 Feign 接口
 *
 * @author 陈黎明
 * @since 2025-03-08
 */

@FeignClient(contextId = "RemoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteUserService {

    @GetMapping("/system/user/info/{username}")
    Result<User> getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/system/user/{userId}")
    Result<User> getUserById(@PathVariable("userId") Long userId);

    /**
     * 更新用户登录信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @PutMapping("/system/user/login-info/{userId}")
    Result<Void> updateLoginInfo(@PathVariable("userId") Long userId);
}
