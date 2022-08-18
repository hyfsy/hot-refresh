package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public interface RpcServer {

    void start() throws ServerException;

    void stop() throws ServerException;

    Message request(String addr, Message message, long timeoutMillis) throws RemotingException;

    void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException;

}
