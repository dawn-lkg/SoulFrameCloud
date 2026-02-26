package com.clm.soulframe.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.clm.soulframe.mybatis.interceptor.SqlInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 * 仅在存在 MyBatis 时才加载
 */
@Configuration
@Slf4j
public class MyBatisConfig {

    /**
     * SQL 拦截器
     * 通过 @Bean 方式注册，确保在 SqlSessionFactory 创建后生效
     */
   @Bean
   public SqlInterceptor sqlInterceptor() {
       log.info("注册 SQL 拦截器");
       return new SqlInterceptor();
   }

   @Bean
   public MybatisPlusInterceptor mybatisPlusInterceptor() {
       MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
       interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
       return interceptor;
   }

}
