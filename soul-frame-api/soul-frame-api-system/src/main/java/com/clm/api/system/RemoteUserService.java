package com.clm.api.system;

import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@FeignClient(contextId = "RemoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteUserService {

    @GetMapping("/system/user/info/{username}")
    Result<User> getUserByUsername(@PathVariable("username") String username);
}
