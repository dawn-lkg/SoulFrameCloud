package com.clm.common.core.exception;

import com.clm.common.core.enums.HttpCodeEnum;
import lombok.Data;

/**
 * 业务异常
 * @author 陈黎明
 * @date 2025/3/1 下午11:05
 */

@Data
public final class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;

    /**
     * 空构造方法，避免反序列化问题
     */
    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(HttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMessage());
        this.code = httpCodeEnum.getCode();
    }
}
