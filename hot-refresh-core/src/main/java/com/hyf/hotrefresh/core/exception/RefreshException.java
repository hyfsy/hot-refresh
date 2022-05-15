package com.hyf.hotrefresh.core.exception;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RefreshException extends Exception {

    public RefreshException(String message) {
        super(message);
    }

    public RefreshException(String message, Throwable cause) {
        super(message, cause);
    }
}
