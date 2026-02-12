package com.clm.common.core.controller;


import com.clm.common.core.domain.Result;
import com.clm.common.core.enums.HttpCodeEnum;

/**
 * @author 陈黎明
 * @date 2025/3/1 下午10:54
 */

@SuppressWarnings("all")
public class BaseController {

    public Result success() {
        return Result.success();
    }

    public <T> Result<T>  success(T data) {
        return Result.success(data);
    }

    public <T> Result success(T data, String msg) {
        return Result.success(data, msg);
    }

    public Result error() {
        return Result.error();
    }

    public Result error(Integer code, String msg) {
        return Result.error(code, msg);
    }

    public Result error(HttpCodeEnum httpCodeEnum) {
        return Result.error(httpCodeEnum);
    }

    public Result error(String msg) {
        return Result.error(HttpCodeEnum.ERROR.getCode(), msg);
    }
}
