package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class ServerException extends Exception {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}