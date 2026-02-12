package com.clm.modules.system.domain.param;

import com.clm.common.core.domain.BasePageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志查询参数
 *
 * @author 陈黎明
 * @since 2025-03-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogQueryParam extends BasePageParam {
    
    /**
     * 用户账号
     */
    private String userName;
    
    /**
     * 登录状态
     */
    private String status;
    
    /**
     * IP地址
     */
    private String ipaddr;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 开始时间
     */
    private String beginTime;
    
    /**
     * 结束时间
     */
    private String endTime;
} 