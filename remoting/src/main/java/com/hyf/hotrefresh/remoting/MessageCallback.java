package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public interface MessageCallback {

    void handle(Message message, Throwable t);

}
