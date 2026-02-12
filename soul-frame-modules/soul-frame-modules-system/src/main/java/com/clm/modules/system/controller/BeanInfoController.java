package com.clm.modules.system.controller;

import com.clm.common.core.constants.Constants;
import com.clm.common.core.controller.BaseController;
import com.clm.common.core.domain.Result;
import com.clm.common.core.domain.entity.BeanMethod;
import com.clm.common.log.enums.BusinessType;
import com.clm.common.core.utils.CommonUtils;
import com.clm.common.log.annotation.Log;
import com.clm.modules.system.properties.TimerTaskPackProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈黎明
 * @since 2025-03-08
 */

@Tag(name = "bean管理")
@RestController
@RequestMapping("/system/bean")
@RequiredArgsConstructor
public class BeanInfoController extends BaseController {

    private final ApplicationContext applicationContext;

    private final TimerTaskPackProperties timerTaskPackProperties;

    @Operation(summary = "获取bean列表")
    @Log(businessType = BusinessType.QUERY)
    @RequestMapping("/list")
    public Result<List<Map<String, List<BeanMethod>>>> list() {
        List<String> pack = timerTaskPackProperties.getPack();
        List<String> beanList = Arrays.asList(applicationContext.getBeanDefinitionNames());
        List<Map<String, List<BeanMethod>>> list = beanList.stream().filter(beanName -> {
            Object bean = applicationContext.getBean(beanName);
            String className = bean.getClass().getName();
            return pack.stream().anyMatch(className::startsWith);
        }).map(beanName -> {
            Map<String, List<BeanMethod>> map = new HashMap<>();
            Object bean = applicationContext.getBean(beanName);
            List<Method> methodList = Arrays.asList(bean.getClass().getMethods());
            List<BeanMethod> beanMethodList = methodList.stream().filter(method -> !Constants.OBJECT_METHODS.contains(method.getName())).map(method -> {
                BeanMethod beanMethod = new BeanMethod();
                String formatMethod = CommonUtils.formatMethodWithParamNames(method);
                String value = beanName + "." + formatMethod;
                beanMethod.setLabel(formatMethod);
                beanMethod.setValue(value);
                return beanMethod;
            }).toList();
            map.put(beanName, beanMethodList);
            return map;
        }).toList();
        return success(list);
    }


}
