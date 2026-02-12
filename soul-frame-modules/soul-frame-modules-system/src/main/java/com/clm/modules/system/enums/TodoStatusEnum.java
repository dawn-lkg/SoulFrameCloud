package com.clm.modules.system.enums;

/**
 * 待办事项状态枚举
 *
 * @author 陈黎明
 * @since 2025-09-13
 */
public enum TodoStatusEnum {

    /**
     * 待办
     */
    PENDING("pending", "待办"),

    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),

    /**
     * 已逾期
     */
    OVERDUE("overdue", "已逾期"),

    /**
     * 进行中
     */
    IN_PROGRESS("in_progress", "进行中"),

    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消");

    private final String code;
    private final String desc;

    TodoStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 状态码
     * @return 枚举实例
     */
    public static TodoStatusEnum getByCode(String code) {
        for (TodoStatusEnum statusEnum : TodoStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     *
     * @param code 状态码
     * @return 状态描述
     */
    public static String getDescByCode(String code) {
        TodoStatusEnum statusEnum = getByCode(code);
        return statusEnum != null ? statusEnum.getDesc() : null;
    }

    /**
     * 判断是否为有效的状态码
     *
     * @param code 状态码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
