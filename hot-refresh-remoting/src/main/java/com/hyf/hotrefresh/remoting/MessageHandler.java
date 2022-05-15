package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public interface MessageHandler {

    void handle(Message message) throws Exception;
}
