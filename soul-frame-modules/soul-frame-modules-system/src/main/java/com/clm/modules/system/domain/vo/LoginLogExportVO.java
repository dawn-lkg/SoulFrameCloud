package com.clm.modules.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author 陈黎明
 * @Date 2025/7/10 19:26
 * @since 2025-03-08
 */

@Data
public class LoginLogExportVO {
    /**
     * 用户账号
     */
    @ExcelProperty("用户账号")
    private String userName;

    /**
     * 登录IP地址
     */
    @ExcelProperty("登录IP地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @ExcelProperty("登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ExcelProperty("浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty("操作系统")
    private String os;

    /**
     * 状态描述
     */
    @Schema(description = "登录状态")
    private String statusDesc;

    /**
     * 提示消息
     */
    @ExcelProperty("提示消息")
    private String msg;

    /**
     * 登录时间
     */
    @ExcelProperty("登录时间")
    private Date loginTime;
}
