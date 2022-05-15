package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public interface MessageHandler {

    Message handle(Message request) throws Exception;
}
