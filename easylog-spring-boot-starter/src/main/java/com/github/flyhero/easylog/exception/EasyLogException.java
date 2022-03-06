package com.github.flyhero.easylog.exception;

/**
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/3/6 15:10
 */
public class EasyLogException extends RuntimeException{

    public EasyLogException() {
    }

    public EasyLogException(String message) {
        super(message);
    }

    public EasyLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyLogException(Throwable cause) {
        super(cause);
    }

    public EasyLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
