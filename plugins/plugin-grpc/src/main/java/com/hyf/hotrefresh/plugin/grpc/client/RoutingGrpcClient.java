package com.hyf.hotrefresh.plugin.grpc.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.client.core.client.DelegateRoutingClient;
import com.hyf.hotrefresh.client.core.client.HttpBasedClient;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class RoutingGrpcClient extends DelegateRoutingClient {

    @Override
    protected Client chooseClient(String serverAddress) {
        return isHttp(serverAddress) ? new HttpBasedClient() : new GrpcClientAdapter();
    }
}
