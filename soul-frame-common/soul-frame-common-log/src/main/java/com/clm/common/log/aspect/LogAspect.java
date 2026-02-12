package com.clm.common.log.aspect;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.clm.api.system.domain.OperLogDTO;
import com.clm.common.core.model.LoginUser;
import com.clm.common.core.utils.IpUtils;
import com.clm.common.core.utils.ServletUtils;
import com.clm.common.core.utils.UserAgentUtils;
import com.clm.common.log.annotation.Log;
import com.clm.common.log.service.AsyncLogService;
import com.clm.common.security.utils.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * 操作日志记录处理
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    @Value("${ip.xdbPath}")
    private String xdbPath;

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREAD_LOCAL = new NamedThreadLocal<>("Cost Time");

    private final AsyncLogService asyncLogService;

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog) {
        TIME_THREAD_LOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param result 返回结果
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object result) {
        handleLog(joinPoint, controllerLog, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 处理日志记录
     *
     * @param joinPoint 切点
     * @param controllerLog 日志注解
     * @param e 异常
     * @param result 返回结果
     */
    protected void handleLog(JoinPoint joinPoint, Log controllerLog, Exception e, Object result) {
        try {
            // 获取当前登录用户
            Long userId = StpUtil.getLoginIdAsLong();
            LoginUser loginUser = AuthenticationUtil.getLoginUser();

            // 构建操作日志对象
            OperLogDTO operLog = new OperLogDTO();
            // 默认为成功
            operLog.setStatus(0);
            // 获取方法摘要
            MethodSignature signature =(MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 记录操作时间和耗时
            long endTime = System.currentTimeMillis();
            long costTime = endTime - TIME_THREAD_LOCAL.get();
            operLog.setOperTime(new Date());
            operLog.setCostTime(costTime);

            // 记录请求URL和方法
            HttpServletRequest request = ServletUtils.getRequest();
            if (request != null) {
                operLog.setOperUrl(request.getRequestURI());
            }
            if (request != null) {
                operLog.setRequestMethod(request.getMethod());
            }

            // 记录操作人和部门
            if (loginUser != null) {
                operLog.setOperName(loginUser.getUsername());
                // TODO: 获取部门名称
                // operLog.setDeptName(user.getDeptName());
            }

            // 记录IP和地理位置
            String ip = IpUtils.getIpAddr(request);
            operLog.setOperIp(ip);
            String operLocaltion = IpUtils.getCityInfoByVectorIndex(ip, xdbPath);
            operLog.setOperLocation(operLocaltion);
            
            // 记录操作系统和浏览器信息
            String userAgent = UserAgentUtils.getUserAgent(request);
            operLog.setOs(UserAgentUtils.getOs(userAgent));
            operLog.setBrowser(UserAgentUtils.getBrowser(userAgent));
            operLog.setUserAgent(userAgent);
            
            // 记录操作类型、标题等
            operLog.setTitle(getSummary(method, controllerLog));
            operLog.setBusinessType(controllerLog.businessType().getCode());
            operLog.setOperatorType(controllerLog.operatorType().getCode());

            //记录调用方法
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            
            // 记录请求参数
            if (controllerLog.isSaveRequestData()) {
                String requestParams = getRequestParams(joinPoint, controllerLog);
                operLog.setOperParam(StrUtil.sub(requestParams, 0, 2000));
            }
            
            // 记录响应结果
            if (controllerLog.isSaveResponseData() && ObjectUtil.isNotNull(result)) {
                String jsonResult = JSONUtil.toJsonStr(result);
                operLog.setJsonResult(StrUtil.sub(jsonResult, 0, 2000));
            }
            
            // 记录异常信息
            if (e != null) {
                operLog.setStatus(1); // 设置为失败状态
                operLog.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 2000));
            }
            
            // 保存操作日志
            asyncLogService.insertOperLogAsync(operLog);
        } catch (NotLoginException exp) {
            log.error("未登录");
        } catch (Exception exp) {
            log.error("记录操作日志发生异常：", exp);
        } finally {
            TIME_THREAD_LOCAL.remove();
        }
    }
    
    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint, Log log) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        
        // 排除指定参数
        String[] excludeParams = log.excludeParamNames();
        if (ArrayUtil.isNotEmpty(excludeParams)) {
            args = Arrays.stream(args)
                    .filter(arg -> !ArrayUtil.contains(excludeParams, arg.getClass().getName()))
                    .toArray();
        }
        
        // 过滤掉不需要的参数
        Object[] filteredArgs = Arrays.stream(args)
                .filter(this::shouldLogParam)
                .toArray();
        
        return JSONUtil.toJsonStr(filteredArgs);
    }
    
    /**
     * 判断是否需要记录参数
     */
    private boolean shouldLogParam(Object arg) {
        if (arg == null) {
            return false;
        }
        
        // 不记录以下类型的参数
        return !(arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                || arg instanceof MultipartFile || arg instanceof BindingResult);
    }

    /**
     * 获取文档注解的操作描述
     * @param method 方法
     * @param log 日志注解
     * @return 操作描述
     */
    private String getSummary(Method method, Log log) {
        if(StrUtil.isNotBlank(log.value())){
            return log.value();
        }
        Operation annotation = method.getAnnotation(Operation.class);
        if(annotation != null && StrUtil.isNotBlank(annotation.summary())){
            return annotation.summary();
        }
        return null;
    }
} 