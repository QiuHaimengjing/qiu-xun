package com.qiu.qiuxun.common;

/**
 * @description: 错误枚举类
 * @className: ErrorCode.java
 * @author: qiu
 * @createTime: 2023/12/16 18:33
 */
public enum ErrorCode {
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40100, "无权限", ""),
    FORBIDDEN(40301, "禁止操作", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");

    private final Integer code;
    private final String message;
    private final String description;

    ErrorCode(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
