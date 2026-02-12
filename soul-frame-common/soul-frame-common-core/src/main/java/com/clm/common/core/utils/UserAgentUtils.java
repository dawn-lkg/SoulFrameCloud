package com.clm.common.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户代理工具类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Slf4j
public class UserAgentUtils {

    /**
     * 获取用户代理
     *
     * @param request 请求
     * @return 用户代理
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
    /**
     * 获取用户代理（从当前请求中获取）
     *
     * @return 用户代理
     */
    public static String getUserAgent() {
        HttpServletRequest request = ServletUtils.getRequest();
        return request != null ? getUserAgent(request) : null;
    }

    /**
     * 获取操作系统
     *
     * @param userAgent 用户代理
     * @return 操作系统
     */
    public static String getOs(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "未知";
        }
        
        if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "Mac OS";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "Linux";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
            return "iOS";
        } else {
            return "其他";
        }
    }

    /**
     * 获取浏览器
     *
     * @param userAgent 用户代理
     * @return 浏览器
     */
    public static String getBrowser(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "未知";
        }
        
        if (userAgent.toLowerCase().contains("edge")) {
            return "Edge";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            return "Firefox";
        } else if (userAgent.toLowerCase().contains("chrome") && !userAgent.toLowerCase().contains("opr")) {
            return "Chrome";
        } else if (userAgent.toLowerCase().contains("safari") && !userAgent.toLowerCase().contains("chrome")) {
            return "Safari";
        } else if (userAgent.toLowerCase().contains("opera") || userAgent.toLowerCase().contains("opr")) {
            return "Opera";
        } else if (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("trident")) {
            return "IE";
        } else {
            return "其他";
        }
    }
    
    /**
     * 格式化耗时
     * 
     * @param costTime 耗时（毫秒）
     * @return 格式化后的耗时
     */
    public static String formatCostTime(long costTime) {
        if (costTime < 1000) {
            return costTime + "毫秒";
        } else if (costTime < 60000) {
            return String.format("%.2f秒", costTime / 1000.0);
        } else {
            return String.format("%.2f分钟", costTime / 60000.0);
        }
    }
} 