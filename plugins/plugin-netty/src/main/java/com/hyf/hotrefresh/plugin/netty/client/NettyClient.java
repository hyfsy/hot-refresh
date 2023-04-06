package com.hyf.hotrefresh.plugin.netty.client;

import com.hyf.hotrefresh.client.api.client.RpcClientAdapter;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class NettyClient extends RpcClientAdapter {

    public NettyClient() {
        super(new NettyRpcClient());
    }
}
