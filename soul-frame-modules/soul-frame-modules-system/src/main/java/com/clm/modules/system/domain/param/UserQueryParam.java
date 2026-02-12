package com.clm.modules.system.domain.param;


import com.clm.common.core.domain.BasePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户查询参数
 *
 * @author 陈黎明
 * @since 2025-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryParam extends BasePageParam {
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String userName;
    
    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;
    
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;
    
    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private String status;
    
    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门id列表
     */
    @Schema(description = "部门id列表")
    private List<Long> deptIds;
} 