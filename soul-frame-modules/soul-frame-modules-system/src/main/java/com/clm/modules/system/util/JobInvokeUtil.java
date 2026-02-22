package com.clm.modules.system.util;

import cn.hutool.core.util.StrUtil;
import com.clm.modules.system.domain.entity.SysJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 任务执行工具
 */
public class JobInvokeUtil {

    private static final Logger log = LoggerFactory.getLogger(JobInvokeUtil.class);

    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception {
        String invokeTarget = sysJob.getInvokeTarget();
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        String methodParams = getMethodParams(invokeTarget);

//        log.info("准备执行任务: bean={}, method={}, params={}", beanName, methodName, methodParams);

        if (StrUtil.isEmpty(beanName)) {
            log.error("调用目标bean名称为空");
            throw new Exception("调用目标bean名称为空");
        }

        // 从Spring容器获取bean并执行方法
        Object bean = SpringUtils.getBean(beanName);
        if (bean == null) {
            log.error("找不到bean: {}", beanName);
            throw new Exception("找不到bean: " + beanName);
        }

        invokeMethod(bean, methodName, methodParams);
    }

    /**
     * 调用任务方法
     *
     * @param bean         目标对象
     * @param methodName   方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, String methodParams) throws Exception {
        if (StrUtil.isEmpty(methodName)) {
            log.error("方法名称为空");
            throw new Exception("方法名称为空");
        }

        if (StrUtil.isNotEmpty(methodParams)) {
            Method method = ReflectionUtils.findMethod(bean.getClass(), methodName, String.class);
            if (method != null) {
                ReflectionUtils.invokeMethod(method, bean, methodParams);
                return;
            }
        }

        Method method = ReflectionUtils.findMethod(bean.getClass(), methodName);
        if (method != null) {
            ReflectionUtils.invokeMethod(method, bean);
        } else {
            throw new Exception("找不到方法: " + bean.getClass().getName() + "." + methodName);
        }
    }

    /**
     * 获取bean名称
     *
     * @param invokeTarget 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget) {
        if (StrUtil.isEmpty(invokeTarget)) {
            return "";
        }
        String beanName = StrUtil.subBefore(invokeTarget, "(", false);
        return StrUtil.subBefore(beanName, ".", false);
    }

    /**
     * 获取bean方法
     *
     * @param invokeTarget 目标字符串
     * @return method方法
     */
    public static String getMethodName(String invokeTarget) {
        if (StrUtil.isEmpty(invokeTarget)) {
            return "";
        }
        String methodName = StrUtil.subBefore(invokeTarget, "(", false);
        return StrUtil.subAfter(methodName, ".", false);
    }

    /**
     * 获取method方法参数
     *
     * @param invokeTarget 目标字符串
     * @return method方法参数
     */
    public static String getMethodParams(String invokeTarget) {
        if (StrUtil.isEmpty(invokeTarget)) {
            return "";
        }
        return StrUtil.subBetween(invokeTarget, "(", ")");
    }
} 