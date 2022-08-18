package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_URL;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class DelegateRoutingClient implements Client {

    private final Client client;

    public DelegateRoutingClient() {
        String serverAddress = ArgumentHolder.get(ARG_SERVER_URL);
        client = chooseClient(serverAddress);
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

    protected Client chooseClient(String serverAddress) {
        return isHttp(serverAddress) ? new HttpBasedClient() : new NativeSocketClient();
    }

    protected boolean isHttp(String address) {
        return address.startsWith("http");
    }

}
