package com.clm.modules.system.timerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务
 */
@Component("testTask")
public class TestTask {

    private static final Logger log = LoggerFactory.getLogger(TestTask.class);

    /**
     * 无参方法
     */
    public void noParams() {
        log.info("执行无参方法");
    }

    /**
     * 有参方法
     */
    public void params(String params) {
        log.info("执行有参方法：" + params);
    }

    public void error(String abc, Integer a, Integer b) {
        log.error("执行有参方法：{},{},{}", abc, a, b);
    }
} 