package com.hyf.hotrefresh.plugin.execute.exception;

/**
 * @author baB_hyf
 * @date 2022/05/21
 */
public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
