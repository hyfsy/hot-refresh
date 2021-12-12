package com.hyf.hotrefresh.exception;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class CompileException extends RefreshException {

    public CompileException() {
        super();
    }

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
