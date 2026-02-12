package com.clm.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据表 实体类
 *
 * @author 陈黎明
 * @date 2025-03-05
 */
@Data
@TableName("sys_dict_data")
public class DictData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @TableId(value = "dict_code", type = IdType.AUTO)
    @Schema(description = "字典编码")
    @NotNull(message = "字典编码不能为空", groups = Update.class)
    private Long dictCode;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    @Schema(description = "字典排序")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @TableField("dict_label")
    @Schema(description = "字典标签")
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField("dict_value")
    @Schema(description = "字典键值")
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @TableField("css_class")
    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    /**
     * 表格回显样式
     */
    @TableField("list_class")
    @Schema(description = "表格回显样式")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @TableField("is_default")
    @Schema(description = "是否默认（Y是 N否）")
    private String isDefault;

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
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
} 