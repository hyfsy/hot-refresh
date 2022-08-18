package com.hyf.hotrefresh.core.exception;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class InstallException extends RefreshException {

    public InstallException(String message) {
        super(message);
    }

    public InstallException(String message, Throwable cause) {
        super(message, cause);
    }
}
