package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型表 实体类
 *
 * @author 陈黎明
 * @date 2025/3/5
 */
@Data
@TableName("sys_dict_type")
public class DictType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @TableId(value = "dict_id", type = IdType.AUTO)
    @Schema(description = "字典主键")
    private Long dictId;

    /**
     * 字典名称
     */
    @TableField("dict_name")
    @Schema(description = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    @Schema(description = "状态（0正常 1停用）")
    @NotBlank(message = "状态不能为空")
    private String status;

    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
} 