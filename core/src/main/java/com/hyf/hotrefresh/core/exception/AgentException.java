package com.hyf.hotrefresh.core.exception;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class AgentException extends RefreshException {

    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }
}
