package com.qiu.qiuxun.exception;

import com.qiu.qiuxun.common.ErrorCode;

/**
 * @description: 业务异常类
 * @className: BusinessException.java
 * @author: qiu
 * @createTime: 2023/12/16 18:46
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -5019992523265246352L;

    private final Integer code;
    private final String description;

    public BusinessException(Integer code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
