package com.clm.api.system;

import com.clm.api.system.domain.RoleSimpleDTO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 远程角色服务 Feign 接口
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@FeignClient(contextId = "RemoteRoleService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteRoleService {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/system/role/user/{userId}")
    Result<List<RoleSimpleDTO>> selectRolesByUserId(@PathVariable("userId") Long userId);
}
