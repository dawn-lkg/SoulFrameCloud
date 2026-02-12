package com.clm.common.core.domain;

import com.clm.common.core.enums.HttpCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一响应结果类
 * @author 陈黎明
 * @date 2025/3/1 下午10:02
 */

@Data
@Accessors(chain = true)
@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1581329103599839148L;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "返回消息", example = "成功")
    private String msg;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "时间戳", example = "1648291200000")
    private Long timestamp;

    /** 成功状态码 */
    private static final Integer SUCCESS_CODE = HttpCodeEnum.SUCCESS.getCode();
    
    /** 成功消息 */
    private static final String SUCCESS_MESSAGE = HttpCodeEnum.SUCCESS.getMessage();
    
    /** 错误状态码 */
    private static final Integer ERROR_CODE = HttpCodeEnum.ERROR.getCode();
    
    /** 错误消息 */
    private static final String ERROR_MESSAGE = HttpCodeEnum.ERROR.getMessage();

    /**
     * 默认构造函数
     */
    public Result() {
        this.success = true;
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MESSAGE;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 根据成功状态构造结果
     * @param success 是否成功
     */
    public Result(Boolean success) {
        this.success = success;
        this.code = success ? SUCCESS_CODE : ERROR_CODE;
        this.msg = success ? SUCCESS_MESSAGE : ERROR_MESSAGE;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 返回成功结果
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>();
    }

    /**
     * 返回带数据的成功结果
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }

    /**
     * 返回带数据和消息的成功结果
     * @param data 数据
     * @param msg 消息
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回错误结果
     * @param <T> 数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(ERROR_CODE);
        result.setMsg(ERROR_MESSAGE);
        return result;
    }

    /**
     * 返回带状态码和消息的错误结果
     * @param code 状态码
     * @param msg 消息
     * @param <T> 数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 返回带消息的错误结果
     * @param msg 消息
     * @param <T> 数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error(String msg) {
        return error(ERROR_CODE, msg);
    }

    /**
     * 返回带状态码的错误结果
     * @param httpCodeEnum 状态码
     * @return <T> 错误结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error(HttpCodeEnum httpCodeEnum) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(httpCodeEnum.getCode());
        result.setMsg(httpCodeEnum.getMessage());
        return result;
    }
}
