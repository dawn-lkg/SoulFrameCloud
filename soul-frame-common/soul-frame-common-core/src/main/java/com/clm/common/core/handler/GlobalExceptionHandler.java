package com.clm.common.core.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.clm.common.core.domain.Result;
import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 陈黎明
 * @date 2025/3/1 下午11:03
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handleNotPermissionException(NotPermissionException e) {
        log.error(e.getMessage(), e);
        return Result.error(HttpCodeEnum.FORBIDDEN.getCode(), "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handleNotRoleException(NotRoleException e) {
        log.error(e.getMessage(), e);
        return Result.error(HttpCodeEnum.FORBIDDEN.getCode(), "没有访问权限，请联系管理员授权");
    }

    /**
     * 认证失败
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e) {
        String message = "";
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效 token";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token无效";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token已过期";
        } else if (e.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token已被顶下线";
        } else if (e.getType().equals(NotLoginException.BE_REPLACED)) {
           message = "token已被顶下线";
        } else if (e.getType().equals(NotLoginException.NO_PREFIX)) {
            message = "未按照指定前缀提交 token";
        } else {
            message = "当前会话未登录";
        }
        return Result.error(HttpCodeEnum.UNAUTHORIZED.getCode(), message);
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return Result.error(HttpCodeEnum.BAD_METHOD.getCode(), "请求方式不支持");
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    public Result<?> handleServiceException(BaseException e) {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return !Objects.isNull(code) ? Result.error(code, e.getMessage()) : Result.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("运行时异常，请求地址: {}，异常信息: {}", requestURI, e.getMessage(), e);
        return Result.error(HttpCodeEnum.ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("系统异常，请求地址: {}，异常信息: {}", requestURI, e.getMessage(), e);
        return Result.error(HttpCodeEnum.ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常，请求地址: {}，异常信息: {}", requestURI, message);
        return Result.error(HttpCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("参数校验异常，请求地址: {}，异常信息: {}", requestURI, message);
        return Result.error(HttpCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 未找到路径异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handlerNoFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址: {}，未找到路径异常", requestURI, e);
        return Result.error(HttpCodeEnum.NOT_FOUND.getCode(), "路径不存在，请检查路径是否正确");
    }
}
