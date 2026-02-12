package com.clm.modules.timer.domain.param;

import com.clm.common.core.domain.BasePageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SysJobLogParam extends BasePageParam {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    private String invokeTarget;

    /**
     * 日志信息
     */
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    private String status;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 停止时间
     */
    private Date stopTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务id
     */
    private Long jobId;
}
