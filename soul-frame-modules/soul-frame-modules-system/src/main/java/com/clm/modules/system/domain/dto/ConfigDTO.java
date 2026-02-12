package com.clm.modules.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置表 数据传输对象
 *
 * @author 陈黎明
 * @date 2025-07-12 19:37:08
 */
@Data
@Schema(description = "系统配置表数据传输对象")
public class ConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 配置项名称
     */
    @Schema(description = "配置项名称")
    private String configName;

    /**
     * 配置项键名
     */
    @Schema(description = "配置项键名")
    private String configKey;

    /**
     * 配置项值
     */
    @Schema(description = "配置项值")
    private String configValue;

    /**
     * 配置项类型(string:字符串 number:数字 boolean:布尔值 json:JSON对象)
     */
    @Schema(description = "配置项类型(string:字符串 number:数字 boolean:布尔值 json:JSON对象)")
    private String configType;

    /**
     * 配置项分组
     */
    @Schema(description = "配置项分组")
    private String configGroup;

    /**
     * 配置项描述
     */
    @Schema(description = "配置项描述")
    private String configDesc;

    /**
     * 是否启用(0:禁用 1:启用)
     */
    @Schema(description = "是否启用(0:禁用 1:启用)")
    private Integer isEnabled;

    /**
     * 是否系统配置(0:否 1:是)
     */
    @Schema(description = "是否系统配置(0:否 1:是)")
    private Integer isSystem;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
