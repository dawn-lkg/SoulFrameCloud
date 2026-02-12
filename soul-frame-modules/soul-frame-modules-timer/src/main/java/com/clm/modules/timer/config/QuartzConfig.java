package com.clm.modules.timer.config;

import com.clm.quartz.util.SpringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Quartz模块配置
 */
@Configuration
@ComponentScan(basePackages = {"com.clm.quartz"})
@MapperScan(basePackages = {"com.clm.quartz.mapper"})
@Import(SpringUtils.class)
public class QuartzConfig {
} 