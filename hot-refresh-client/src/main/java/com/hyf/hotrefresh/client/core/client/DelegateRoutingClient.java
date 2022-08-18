package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class DelegateRoutingClient implements Client {

    private final Client client;

    public DelegateRoutingClient(String serverAddress) {
        client = isHttp(serverAddress) ? new HttpBasedClient() :
                nettyExist() ? new NettyClient() : new NativeRpcClient();
    }

    @Override
    public void start() {
        client.start();
    }

    @Override
    public void stop() {
        client.stop();
    }

    @Override
    public Message sync(String addr, Message message, long timeoutMillis) throws ClientException {
        return client.sync(addr, message, timeoutMillis);
    }

    @Override
    public void async(String addr, Message message, long timeoutMillis, MessageCallback callback) throws ClientException {
        client.async(addr, message, timeoutMillis, callback);
    }

    private boolean isHttp(String address) {
        return address.startsWith("http");
    }

    private boolean nettyExist() {
        return ReflectionUtils.exists("io.netty.bootstrap.Bootstrap");
    }

}
