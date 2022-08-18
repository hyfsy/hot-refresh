package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;

/**
 * 兼容http和rpc
 *
 * @author baB_hyf
 * @date 2022/08/18
 */
public interface Client {

    void start() throws ClientException;

    void stop() throws ClientException;

    Message sync(String addr, Message message, long timeoutMillis) throws ClientException;

    void async(String addr, Message message, long timeoutMillis, MessageCallback callback) throws ClientException;

}
