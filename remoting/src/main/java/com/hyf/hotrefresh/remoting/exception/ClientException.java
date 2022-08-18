package com.hyf.hotrefresh.remoting.exception;

/**
 * @author baB_hyf
 * @date 2022/07/22
 */
public class ClientException extends RuntimeException {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
