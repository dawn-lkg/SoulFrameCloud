package com.clm.common.log.enums;

import lombok.Getter;

/**
 * 业务操作类型
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Getter
public enum BusinessType {
    /**
     * 查询
     */
    QUERY(0,"查询"),
    
    /**
     * 新增
     */
    INSERT(1,"新增"),
    
    /**
     * 修改
     */
    UPDATE(2,"修改"),
    
    /**
     * 删除
     */
    DELETE(3,"删除"),
    
    /**
     * 导出
     */
    EXPORT(4,"导出"),
    
    /**
     * 导入
     */
    IMPORT(5,"导入"),
    
    /**
     * 清空数据
     */
    CLEAN(6,"清空数据"),
    
    /**
     * 强制操作
     */
    FORCE(7,"强制操作"),
    /**
     * 校验
     */
    CHECK(8, "校验"),
    /**
     * 其他
     */
    OTHER(99,"其他");
    
    private final Integer code;

    private final String name;
    
    BusinessType(Integer code, String name) {
        this.name=name;
        this.code = code;
    }

} 