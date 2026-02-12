package com.clm.modules.system.enums;

import lombok.Getter;

/**
 * 待办事项优先级枚举
 *
 * @author admin
 */
@Getter
public enum TodoPriorityEnum {
    /**
     * 低优先级
     */
    LOW("0", "低", "low"),

    /**
     * 普通优先级
     */
    NORMAL("1", "普通", "normal"),

    /**
     * 高优先级
     */
    HIGH("2", "高", "high"),

    /**
     * 紧急
     */
    URGENT("3", "紧急", "urgent");

    private final String code;
    private final String label;
    private final String value;

    TodoPriorityEnum(String code, String label, String value) {
        this.code = code;
        this.label = label;
        this.value = value;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 优先级代码
     * @return 优先级枚举
     */
    public static TodoPriorityEnum getByCode(String code) {
        if (code == null) {
            return NORMAL;
        }
        for (TodoPriorityEnum priority : TodoPriorityEnum.values()) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        return NORMAL;
    }

    /**
     * 根据value获取枚举
     *
     * @param value 优先级值
     * @return 优先级枚举
     */
    public static TodoPriorityEnum getByValue(String value) {
        if (value == null || value.isEmpty()) {
            return NORMAL;
        }
        for (TodoPriorityEnum priority : TodoPriorityEnum.values()) {
            if (priority.getValue().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        return NORMAL;
    }

    /**
     * 根据code获取label
     *
     * @param code 优先级代码
     * @return 优先级标签
     */
    public static String getLabelByCode(String code) {
        TodoPriorityEnum priority = getByCode(code);
        return priority.getLabel();
    }

    /**
     * 根据value获取label
     *
     * @param value 优先级值
     * @return 优先级标签
     */
    public static String getLabelByValue(String value) {
        TodoPriorityEnum priority = getByValue(value);
        return priority.getLabel();
    }

}

