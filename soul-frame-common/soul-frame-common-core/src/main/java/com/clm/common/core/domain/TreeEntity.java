package com.clm.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构实体基类
 * 
 * @author 陈黎明
 * @date 2025/3/2
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 父节点ID */
    private Long parentId;

    /** 父节点名称 */
    @TableField(exist = false)
    private String parentName;

    /** 祖级列表 */
    private String ancestors;

    /** 显示顺序 */
    private Integer orderNum;

    /** 子节点 */
    @TableField(exist = false)
    private List<TreeEntity> children = new ArrayList<>();
} 