package com.clm.common.core.domain;

import com.clm.common.core.enums.HttpCodeEnum;
import com.clm.common.core.exception.BaseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    // ==================== 静态工厂方法 ====================

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

    // ==================== 链式判断方法 ====================

    /**
     * 成功时执行操作
     * @param action 成功时执行的操作
     * @return this
     */
    public Result<T> onSuccess(Consumer<T> action) {
        if (isSuccess() && data != null) {
            action.accept(data);
        }
        return this;
    }

    /**
     * 失败时执行操作
     * @param action 失败时执行的操作
     * @return this
     */
    public Result<T> onFailure(Consumer<String> action) {
        if (isFailure()) {
            action.accept(msg);
        }
        return this;
    }

    /**
     * 成功时返回指定值，失败时返回 null
     * @param <R> 返回类型
     * @return 成功时的值或 null
     */
    @JsonIgnore
    public <R> R getOrNull() {
        return isSuccess() ? (R) data : null;
    }

    /**
     * 成功时返回指定值，失败时返回默认值
     * @param defaultValue 失败时的默认值
     * @param <R> 返回类型
     * @return 成功时的值或默认值
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <R> R getOrElse(R defaultValue) {
        return isSuccess() ? (R) data : defaultValue;
    }

    /**
     * 成功时返回指定值，失败时执行 supplier 获取值
     * @param supplier 失败时的值提供者
     * @param <R> 返回类型
     * @return 成功时的值或 supplier 获取的值
     */
    @SuppressWarnings("unchecked")
    public <R> R getOrSupply(Supplier<R> supplier) {
        return isSuccess() ? (R) data : supplier.get();
    }

    /**
     * 成功时返回数据，失败时抛出异常
     * @param <R> 返回类型
     * @return 成功时的数据
     * @throws RuntimeException 失败时抛出的异常
     */
    @SuppressWarnings("unchecked")
    public <R> R getOrThrow() {
        if (isSuccess()) {
            return (R) data;
        }
        throw new BaseException(msg);
    }

    /**
     * 成功时返回数据，失败时抛出指定异常
     * @param exceptionSupplier 异常提供者
     * @param <R> 返回类型
     * @param <X> 异常类型
     * @return 成功时的数据
     * @throws X 失败时抛出的异常
     */
    @SuppressWarnings("unchecked")
    public <R, X extends Throwable> R getOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isSuccess()) {
            return (R) data;
        }
        throw exceptionSupplier.get();
    }

    /**
     * 转换为 Optional
     * @return 成功时返回 Optional.of(data)，失败时返回 Optional.empty()
     */
    public Optional<T> toOptional() {
        return isSuccess() ? Optional.ofNullable(data) : Optional.empty();
    }

    // ==================== 映射方法 ====================

    /**
     * 成功时映射数据
     * @param mapper 映射函数
     * @param <R> 目标类型
     * @return 映射后的 Result
     */
    public <R> Result<R> map(Function<T, R> mapper) {
        if (isSuccess() && data != null) {
            return Result.success(mapper.apply(data)).setMsg(msg).setCode(code);
        }
        return Result.<R>error(code, msg);
    }

    /**
     * 成功时映射数据，失败时返回新的错误 Result
     * @param mapper 映射函数
     * @param <R> 目标类型
     * @return 映射后的 Result
     */
    public <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
        if (isSuccess() && data != null) {
            return mapper.apply(data);
        }
        return Result.<R>error(code, msg);
    }

    /**
     * 失败时映射错误消息
     * @param mapper 映射函数
     * @param <R> 目标类型
     * @return 映射后的 Result
     */
    public <R> Result<R> mapError(Function<String, String> mapper) {
        if (isFailure()) {
            return Result.<R>error(code, mapper.apply(msg));
        }
        return (Result<R>) this;
    }

    /**
     * 根据条件转换数据
     * @param mapper 映射函数
     * @param <R> 目标类型
     * @return 转换后的 Result
     */
    public <R> Result<R> filterAndMap(Function<T, R> mapper, Function<T, String> errorMsg) {
        if (isSuccess() && data != null) {
            T value = data;
            R result = mapper.apply(value);
            if (result != null) {
                return Result.success(result);
            }
            return Result.<R>error(errorMsg.apply(value));
        }
        return Result.<R>error(code, msg);
    }

    // ==================== 判断方法 ====================

    /**
     * 是否成功
     * @return 是否成功
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }

    /**
     * 是否失败
     * @return 是否失败
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * 成功代码相等
     * @param code 状态码
     * @return 是否相等
     */
    public boolean isCode(Integer code) {
        return this.code != null && this.code.equals(code);
    }
}
