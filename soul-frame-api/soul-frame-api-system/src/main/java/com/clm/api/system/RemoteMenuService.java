package com.clm.api.system;

import com.clm.api.system.domain.RouterDTO;
import com.clm.common.core.constants.ServiceNameConstants;
import com.clm.common.core.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 远程菜单服务
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@FeignClient(contextId = "RemoteMenuService", value = ServiceNameConstants.SYSTEM_SERVICE)
public interface RemoteMenuService {

    /**
     * 根据用户ID获取路由菜单
     *
     * @param userId 用户ID
     * @return 路由菜单列表
     */
    @GetMapping("/system/menu/router/{userId}")
    Result<List<RouterDTO>> getRouterByUserId(@PathVariable("userId") Long userId);
}
