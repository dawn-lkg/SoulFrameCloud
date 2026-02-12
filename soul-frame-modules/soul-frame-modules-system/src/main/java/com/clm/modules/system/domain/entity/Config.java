package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clm.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 系统配置表 实体类
 *
 * @author 陈黎明
 * @date 2025-07-12 19:37:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class Config extends BaseEntity {
    
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("id")
    private Long id;

    /**
     * 配置项名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置项键名
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置项值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置项类型(string:字符串 number:数字 boolean:布尔值 json:JSON对象)
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置项分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 配置项描述
     */
    @TableField("config_desc")
    private String configDesc;

    /**
     * 是否启用(0:禁用 1:启用)
     */
    @TableField("is_enabled")
    private Integer isEnabled;

    /**
     * 是否系统配置(0:否 1:是)
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

}
