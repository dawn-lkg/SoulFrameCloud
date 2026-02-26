package com.clm.test.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * Seata 数据源后置处理器
 * 
 * 作用：在 dynamic-datasource 创建数据源后，自动将其包装成 Seata DataSourceProxy
 * 这样 Seata 才能正确识别每个数据库作为独立的资源
 */
@Configuration
public class SeataDataSourceWrapperProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(SeataDataSourceWrapperProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 只处理 DruidDataSource（dynamic-datasource 使用 Druid）
        if (bean instanceof DruidDataSource) {
            // 避免重复代理
            if (bean instanceof DataSourceProxy) {
                return bean;
            }
            
            DruidDataSource druidDataSource = (DruidDataSource) bean;
            String url = druidDataSource.getUrl();
            
            // 判断是哪个数据源
            if (url != null && url.contains("stock_db")) {
                log.info("【Seata后置处理】代理 stock 数据源, URL: {}", url);
                return new DataSourceProxy(druidDataSource);
            } else if (url != null && url.contains("order_db")) {
                log.info("【Seata后置处理】代理 order 数据源, URL: {}", url);
                return new DataSourceProxy(druidDataSource);
            }
        }
        return bean;
    }
}
