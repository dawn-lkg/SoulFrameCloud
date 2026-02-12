package com.clm.common.core.enums;

/**
 * @author 陈黎明
 * @date 2025/3/4 下午10:59
 */
public enum AccountStatus {
    ACTIVE("0","启用"),
    DISABLED("1","禁用");

    private final String code;
    private final String description;

    AccountStatus(String code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public String getCode() {
        return code;
    }
}

