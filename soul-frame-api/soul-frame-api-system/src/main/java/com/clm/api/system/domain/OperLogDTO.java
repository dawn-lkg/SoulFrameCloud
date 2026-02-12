package com.clm.api.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录 DTO（用于跨服务传输）
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Data
public class OperLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 日志主键
     */
    private Long operId;
    
    /**
     * 模块标题
     */
    private String title;
    
    /**
     * 业务类型（0查询 1新增 2修改 3删除 4其他）
     */
    private Integer businessType;
    
    /**
     * 方法名称
     */
    private String method;
    
    /**
     * 请求方式
     */
    private String requestMethod;
    
    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;
    
    /**
     * 操作人员
     */
    private String operName;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 请求URL
     */
    private String operUrl;
    
    /**
     * 主机地址
     */
    private String operIp;
    
    /**
     * 操作地点
     */
    private String operLocation;
    
    /**
     * 请求参数
     */
    private String operParam;
    
    /**
     * 返回参数
     */
    private String jsonResult;
    
    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;
    
    /**
     * 错误消息
     */
    private String errorMsg;
    
    /**
     * 操作时间
     */
    private Date operTime;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 浏览器类型
     */
    private String browser;
    
    /**
     * 耗费时间（毫秒）
     */
    private Long costTime;

    /**
     * 用户代理
     */
    private String userAgent;
}

