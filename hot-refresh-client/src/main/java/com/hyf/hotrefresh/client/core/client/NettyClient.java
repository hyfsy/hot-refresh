package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.plugin.netty.client.NettyRpcClient;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class NettyClient extends RpcClientAdapter {

    public NettyClient() {
        super(new NettyRpcClient());
    }
}
