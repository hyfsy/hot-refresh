package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class RemotingException extends Exception {

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
