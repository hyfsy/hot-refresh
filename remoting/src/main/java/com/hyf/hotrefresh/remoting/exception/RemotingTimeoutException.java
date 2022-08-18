package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public class RemotingTimeoutException extends RemotingException {

    public RemotingTimeoutException(String message) {
        super(message);
    }

    public RemotingTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
