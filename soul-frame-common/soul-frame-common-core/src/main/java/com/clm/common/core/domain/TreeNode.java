package com.clm.common.core.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 树节点基类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
@Accessors(chain = true)
public class TreeNode<T> {
    
    /**
     * 节点ID
     */
    private Long id;
    
    /**
     * 父节点ID
     */
    private Long parentId;
    
    /**
     * 节点名称
     */
    private String label;
    
    /**
     * 子节点列表
     */
    private List<T> children;
    
    /**
     * 是否为叶子节点
     */
    private boolean leaf = true;
    
    /**
     * 层级
     */
    private Integer level;
    
    /**
     * 排序号
     */
    private Integer orderNum;
} 