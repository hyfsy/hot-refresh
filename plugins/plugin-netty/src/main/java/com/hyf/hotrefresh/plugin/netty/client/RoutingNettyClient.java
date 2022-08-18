package com.hyf.hotrefresh.plugin.netty.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.client.core.client.DelegateRoutingClient;
import com.hyf.hotrefresh.client.core.client.HttpBasedClient;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class RoutingNettyClient extends DelegateRoutingClient {

    @Override
    protected Client chooseClient(String serverAddress) {
        return isHttp(serverAddress) ? new HttpBasedClient() : new NettyClient();
    }
}
