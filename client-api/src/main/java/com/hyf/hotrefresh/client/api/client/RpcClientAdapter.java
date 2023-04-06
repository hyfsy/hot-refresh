package com.hyf.hotrefresh.client.api.client;

import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.client.RpcClient;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class RpcClientAdapter implements Client {

    private final RpcClient client;

    public RpcClientAdapter(RpcClient client) {
        this.client = client;
    }

    @Override
    public void start() throws ClientException {
        client.start();
    }

    @Override
    public void stop() throws ClientException {
        client.stop();
    }

    @Override
    public Message sync(String addr, Message message, long timeoutMillis) throws ClientException {
        try {
            return client.request(addr, message, timeoutMillis);
        } catch (RemotingException e) {
            throw new ClientException("Failed to handle request", e);
        }
    }

    @Override
    public void async(String addr, Message message, long timeoutMillis, MessageCallback callback) throws ClientException {
        try {
            client.requestAsync(addr, message, timeoutMillis, callback);
        } catch (RemotingException e) {
            throw new ClientException("Failed to handle request", e);
        }
    }
}
