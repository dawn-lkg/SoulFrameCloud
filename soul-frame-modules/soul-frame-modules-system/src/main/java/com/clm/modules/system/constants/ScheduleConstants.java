package com.clm.modules.system.constants;

/**
 * 调度任务常量
 */
public class ScheduleConstants {

    /**
     * 任务调度参数key
     */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /**
     * 默认
     */
    public static final String MISFIRE_DEFAULT = "0";

    /**
     * 立即触发执行
     */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /**
     * 触发一次执行
     */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /**
     * 不触发立即执行
     */
    public static final String MISFIRE_DO_NOTHING = "3";

    /**
     * 任务状态：正常
     */
    public static final String NORMAL = "0";

    /**
     * 任务状态：暂停
     */
    public static final String PAUSE = "1";
} 