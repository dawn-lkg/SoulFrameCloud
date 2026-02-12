package com.clm.common.core.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对象转换工具类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Slf4j
public class ObjectConvertUtils {

    /**
     * 将对象转换为指定类型
     *
     * @param obj   源对象
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象，转换失败返回null
     */
    public static <T> T convert(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        
        // 如果对象已经是目标类型，直接返回
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        
        T result = null;
        
        try {
            // 1. 尝试通过JSON转换
            result = convertViaJson(obj, clazz);
            if (result != null) {
                return result;
            }
            
            // 2. 尝试通过BeanCopy转换
            result = convertViaBeanCopy(obj, clazz);
            if (result != null) {
                return result;
            }
            
            // 3. 尝试通过Map转换
            if (obj instanceof Map) {
                result = convertViaMap((Map<?, ?>) obj, clazz);
                if (result != null) {
                    return result;
                }
            }
            
            // 4. 尝试通过反射调用getter/setter方法转换
            result = convertViaReflection(obj, clazz);
            
        } catch (Exception e) {
            log.error("对象转换失败：从 {} 转换到 {}", obj.getClass().getName(), clazz.getName(), e);
        }
        
        return result;
    }
    
    /**
     * 将集合转换为指定类型的集合
     *
     * @param collection 源集合
     * @param clazz      目标元素类型
     * @param <T>        泛型
     * @return 转换后的集合
     */
    public static <T> List<T> convertList(Iterable<?> collection, Class<T> clazz) {
        if (collection == null) {
            return null;
        }
        
        List<T> result = new ArrayList<>();
        for (Object obj : collection) {
            T converted = convert(obj, clazz);
            if (converted != null) {
                result.add(converted);
            }
        }
        
        return result;
    }
    
    /**
     * 通过JSON方式转换对象
     */
    private static <T> T convertViaJson(Object obj, Class<T> clazz) {
        try {
            String jsonStr = JSONUtil.toJsonStr(obj);
            return JSONUtil.toBean(jsonStr, clazz);
        } catch (Exception e) {
            log.debug("通过JSON转换对象失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 通过BeanCopy方式转换对象
     */
    private static <T> T convertViaBeanCopy(Object obj, Class<T> clazz) {
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(obj, target, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            return target;
        } catch (Exception e) {
            log.debug("通过BeanCopy转换对象失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 通过Map方式转换对象
     */
    private static <T> T convertViaMap(Map<?, ?> map, Class<T> clazz) {
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    String propertyName = entry.getKey().toString();
                    setProperty(target, propertyName, entry.getValue());
                }
            }
            
            return target;
        } catch (Exception e) {
            log.debug("通过Map转换对象失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 通过反射方式转换对象
     */
    private static <T> T convertViaReflection(Object obj, Class<T> clazz) {
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            Method[] sourceMethods = obj.getClass().getMethods();
            
            for (Method sourceMethod : sourceMethods) {
                String methodName = sourceMethod.getName();
                
                // 处理getter方法
                if (methodName.startsWith("get") && !methodName.equals("getClass") && sourceMethod.getParameterCount() == 0) {
                    String propertyName = methodName.substring(3);
                    if (propertyName.length() > 0) {
                        // 转换首字母为小写
                        propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                        
                        // 获取属性值
                        Object value = sourceMethod.invoke(obj);
                        if (value != null) {
                            // 设置属性值
                            setProperty(target, propertyName, value);
                        }
                    }
                }
            }
            
            return target;
        } catch (Exception e) {
            log.debug("通过反射转换对象失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 设置对象属性值
     */
    private static void setProperty(Object target, String propertyName, Object value) {
        try {
            // 使用hutool的反射工具设置属性值
            ReflectUtil.setFieldValue(target, propertyName, value);
        } catch (Exception ignored) {
            // 尝试通过setter方法设置
            try {
                String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                Method setter = target.getClass().getMethod(setterName, value.getClass());
                setter.invoke(target, value);
            } catch (Exception e) {
                log.trace("设置属性{}失败：{}", propertyName, e.getMessage());
            }
        }
    }


}