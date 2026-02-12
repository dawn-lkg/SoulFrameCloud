package com.clm.modules.system.domain.entity;


import com.clm.modules.system.domain.vo.RoleSimpleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 陈黎明
 * @date 2025-03-08
 */

@Data
public class UserInfo {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色")
    private List<RoleSimpleVO> roles;
}
