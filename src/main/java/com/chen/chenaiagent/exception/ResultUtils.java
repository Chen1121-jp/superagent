package com.chen.chenaiagent.exception;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "success");
    }
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
    public static BaseResponse<?> error(BusinessException e) {
        return new BaseResponse<>(e.getCode(), null, e.getMessage());
    }

}
