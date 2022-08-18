package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public class RemotingExecutionException extends RemotingException {

    public RemotingExecutionException(String message) {
        super(message);
    }

    public RemotingExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
