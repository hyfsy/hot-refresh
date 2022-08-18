package com.hyf.hotrefresh.remoting.client;

import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public interface RpcClient {

    void start() throws ClientException;

    void stop() throws ClientException;

    Message request(String addr, Message message, long timeoutMillis) throws RemotingException;

    void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException;

}
