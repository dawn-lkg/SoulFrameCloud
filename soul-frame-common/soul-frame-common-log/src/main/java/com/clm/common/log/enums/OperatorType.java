package com.clm.common.log.enums;

/**
 * 操作人类别
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
public enum OperatorType {
    /**
     * 其它
     */
    OTHER(0,"其它"),
    
    /**
     * 后台用户
     */
    MANAGE(1,"后台用户"),
    
    /**
     * 手机端用户
     */
    MOBILE(2,"手机端用户");
    
    private final Integer code;

    private final String name;
    
    OperatorType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
} 