package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public class RemotingInterruptedException extends RemotingException {

    public RemotingInterruptedException(String message) {
        super(message);
    }

    public RemotingInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
