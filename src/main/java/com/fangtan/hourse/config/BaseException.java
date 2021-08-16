package com.fangtan.hourse.config;

/**
 * @Description 异常基类
 * @Author Cywen
 * @Date 2019/04/18
 * @Copyright 花吧分期
 */
public class BaseException extends RuntimeException {

    public String code;
    public String msg;

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
