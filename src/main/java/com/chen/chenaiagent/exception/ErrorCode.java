package com.chen.chenaiagent.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(0, "success"),
    PARAM_ERROR(10001, "参数错误"),
    USER_NOT_EXIST(10002, "用户不存在"),
    USER_PASSWORD_ERROR(10003, "用户密码错误"),
    USER_NOT_LOGIN(10004, "用户未登录"),
    NO_PERMISSION(10005, "用户无权限"),
    SYSTEM_ERROR(10006, "系统内部错误"),
    OPERATION_ERROR(10007, "操作失败"),
    AI_TIMEOUT(10008, "AI响应超时"),
    AI_TOKEN_OVER_LIMIT(10010, "Token超限");
    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
