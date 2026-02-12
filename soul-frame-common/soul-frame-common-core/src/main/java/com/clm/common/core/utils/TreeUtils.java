package com.clm.common.core.utils;


import com.clm.common.core.domain.TreeNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形结构工具类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
public class TreeUtils {

    /**
     * 构建树形结构
     *
     * @param list 数据列表
     * @param idGetter ID获取函数
     * @param parentIdGetter 父ID获取函数
     * @param childrenGetter 子节点获取函数
     * @param childrenSetter 子节点设置函数
     * @param <T> 节点类型
     * @param <I> ID类型
     * @return 树形结构列表
     */
    public static <T, I> List<T> buildTree(Collection<T> list,
                                         Function<T, I> idGetter,
                                         Function<T, I> parentIdGetter,
                                         Function<T, List<T>> childrenGetter,
                                         Function<T, Void> childrenSetter) {
        return buildTree(list, idGetter, parentIdGetter, childrenGetter, childrenSetter, null, null, Integer.MAX_VALUE);
    }

    /**
     * 构建树形结构（带层级和排序）
     *
     * @param list 数据列表
     * @param idGetter ID获取函数
     * @param parentIdGetter 父ID获取函数
     * @param childrenGetter 子节点获取函数
     * @param childrenSetter 子节点设置函数
     * @param levelSetter 层级设置函数
     * @param comparator 排序比较器
     * @param maxDepth 最大深度
     * @param <T> 节点类型
     * @param <I> ID类型
     * @return 树形结构列表
     */
    public static <T, I> List<T> buildTree(Collection<T> list,
                                         Function<T, I> idGetter,
                                         Function<T, I> parentIdGetter,
                                         Function<T, List<T>> childrenGetter,
                                         Function<T, Void> childrenSetter,
                                         Function<T, Integer> levelSetter,
                                         Comparator<T> comparator,
                                         int maxDepth) {
        // 转换为Map便于查找
        Map<I, T> nodeMap = list.stream().collect(Collectors.toMap(idGetter, Function.identity(), (k1, k2) -> k1));
        
        List<T> roots = new ArrayList<>();
        
        // 遍历节点列表构建树形结构
        for (T node : list) {
            I parentId = parentIdGetter.apply(node);
            if (isRoot(parentId)) {
                // 父节点为空或0，作为根节点
                roots.add(node);
                // 设置根节点层级为1
                if (levelSetter != null) {
                    levelSetter.apply(node);
                }
            } else {
                // 将当前节点添加到父节点的子节点列表中
                T parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    List<T> children = childrenGetter.apply(parentNode);
                    if (children == null) {
                        children = new ArrayList<>();
                        childrenSetter.apply(parentNode);
                    }
                    children.add(node);
                    
                    // 设置子节点层级
                    if (levelSetter != null) {
                        Integer parentLevel = getNodeLevel(parentNode);
                        if (parentLevel != null && parentLevel < maxDepth) {
                            levelSetter.apply(node);
                        }
                    }
                }
            }
        }
        
        // 递归排序
        if (comparator != null) {
            sortTree(roots, childrenGetter, comparator);
        }
        
        return roots;
    }
    
    /**
     * 递归排序树形结构
     */
    private static <T> void sortTree(List<T> nodes,
                                   Function<T, List<T>> childrenGetter,
                                   Comparator<T> comparator) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        
        // 对当前层级排序
        nodes.sort(comparator);
        
        // 对子节点递归排序
        for (T node : nodes) {
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                sortTree(children, childrenGetter, comparator);
            }
        }
    }
    
    /**
     * 获取节点层级
     */
    private static <T> Integer getNodeLevel(T node) {
        if (node instanceof TreeNode) {
            return ((TreeNode<?>) node).getLevel();
        }
        return null;
    }
    
    /**
     * 判断是否为根节点
     */
    private static boolean isRoot(Object id) {
        if (id == null) {
            return true;
        }
        if (id instanceof Number) {
            return ((Number) id).longValue() == 0;
        }
        if (id instanceof String) {
            return "0".equals(id) || "".equals(id);
        }
        return false;
    }
    
    /**
     * 获取所有子节点ID（可限制深度）
     */
    public static <T, I> List<I> getChildrenIds(T node,
                                              Function<T, I> idGetter,
                                              Function<T, List<T>> childrenGetter,
                                              int maxDepth) {
        List<I> ids = new ArrayList<>();
        if (node == null || maxDepth < 0) {
            return ids;
        }
        
        // 添加当前节点ID
        ids.add(idGetter.apply(node));
        
        // 递归添加子节点ID
        if (maxDepth > 0) {
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                for (T child : children) {
                    ids.addAll(getChildrenIds(child, idGetter, childrenGetter, maxDepth - 1));
                }
            }
        }
        
        return ids;
    }
    
    /**
     * 获取所有子节点ID
     */
    public static <T, I> List<I> getChildrenIds(T node,
                                              Function<T, I> idGetter,
                                              Function<T, List<T>> childrenGetter) {
        return getChildrenIds(node, idGetter, childrenGetter, Integer.MAX_VALUE);
    }
    
    /**
     * 获取所有父节点ID
     */
    public static <T, I> List<I> getParentIds(Collection<T> list,
                                            I currentId,
                                            Function<T, I> idGetter,
                                            Function<T, I> parentIdGetter) {
        List<I> ids = new ArrayList<>();
        if (currentId == null || list == null || list.isEmpty()) {
            return ids;
        }
        
        // 转换为Map便于查找
        Map<I, T> nodeMap = list.stream().collect(Collectors.toMap(idGetter, Function.identity(), (k1, k2) -> k1));
        
        // 递归查找父节点
        T current = nodeMap.get(currentId);
        while (current != null) {
            I parentId = parentIdGetter.apply(current);
            if (isRoot(parentId)) {
                break;
            }
            ids.add(parentId);
            current = nodeMap.get(parentId);
        }
        
        return ids;
    }
    
    /**
     * 递归遍历树形结构（可限制深度）
     */
    public static <T> void traverse(T node,
                                 Function<T, List<T>> childrenGetter,
                                 Function<T, Void> consumer,
                                 int maxDepth) {
        if (node == null || maxDepth < 0) {
            return;
        }
        
        // 处理当前节点
        consumer.apply(node);
        
        // 递归处理子节点
        if (maxDepth > 0) {
            List<T> children = childrenGetter.apply(node);
            if (children != null && !children.isEmpty()) {
                for (T child : children) {
                    traverse(child, childrenGetter, consumer, maxDepth - 1);
                }
            }
        }
    }
    
    /**
     * 递归遍历树形结构
     */
    public static <T> void traverse(T node,
                                 Function<T, List<T>> childrenGetter,
                                 Function<T, Void> consumer) {
        traverse(node, childrenGetter, consumer, Integer.MAX_VALUE);
    }
    
    /**
     * 计算树的深度
     */
    public static <T> int getTreeDepth(T node, Function<T, List<T>> childrenGetter) {
        if (node == null) {
            return 0;
        }
        
        List<T> children = childrenGetter.apply(node);
        if (children == null || children.isEmpty()) {
            return 1;
        }
        
        int maxChildDepth = 0;
        for (T child : children) {
            int childDepth = getTreeDepth(child, childrenGetter);
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }
        
        return maxChildDepth + 1;
    }
}
