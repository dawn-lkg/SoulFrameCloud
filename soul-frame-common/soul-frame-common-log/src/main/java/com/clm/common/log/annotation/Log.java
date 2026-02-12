package com.clm.common.log.annotation;



import com.clm.common.log.enums.BusinessType;
import com.clm.common.log.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块标题
     */
    String value() default "";
    
    /**
     * 业务类型
     */
    BusinessType businessType() default BusinessType.OTHER;
    
    /**
     * 操作人类别
     */
    OperatorType operatorType() default OperatorType.MANAGE;
    
    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;
    
    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;
    
    /**
     * 排除指定的请求参数
     */
    String[] excludeParamNames() default {};
} 