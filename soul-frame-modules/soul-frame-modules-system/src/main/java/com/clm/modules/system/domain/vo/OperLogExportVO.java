package com.clm.modules.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 陈黎明
 * @Date 2025/7/9 13:57
 * @since 2025-03-08
 */

@Data
public class OperLogExportVO {

    /**
     * 模块标题
     */
    @ExcelProperty("标题")
    private String title;

    /**
     * 业务类型描述
     */
    @ExcelProperty("操作")
    private String businessTypeDesc;

    /**
     * 方法名称
     */
    @ExcelProperty("方法")
    private String method;

    /**
     * 请求方式
     */
    @ExcelProperty("请求方式")
    private String requestMethod;
    /**
     * 操作类别描述
     */
    @ExcelProperty("操作类别描述")
    private String operatorTypeDesc;

    /**
     * 操作人员
     */
    @ExcelProperty("操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @ExcelProperty("部门名称")
    private String deptName;

    /**
     * 请求URL
     */
    @ExcelProperty("请求URL")
    private String operUrl;

    /**
     * 主机地址
     */
    @ExcelProperty("主机地址")
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelProperty("地址")
    private String operLocation;

    /**
     * 请求参数
     */
    @ExcelProperty("请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @ExcelProperty("返回参数")
    private String jsonResult;

    /**
     * 操作状态描述
     */
    @ExcelProperty("操作状态")
    private String statusDesc;

    /**
     * 错误消息
     */
    @ExcelProperty("错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @ExcelProperty("操作时间")
    private Date operTime;

    /**
     * 操作系统
     */
    @ExcelProperty("操作系统")
    private String os;

    /**
     * 浏览器类型
     */
    @ExcelProperty("浏览器类型")
    private String browser;
    /**
     * 格式化后的耗费时间
     */
    @ExcelProperty("格式化后的耗费时间")
    private String costTimeDesc;
}
