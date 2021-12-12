package com.hyf.hotrefresh.exception;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class AgentException extends RefreshException {

    public AgentException() {
        super();
    }

    public AgentException(String message) {
        super(message);
    }

    public AgentException(String message, Throwable cause) {
        super(message, cause);
    }
}
