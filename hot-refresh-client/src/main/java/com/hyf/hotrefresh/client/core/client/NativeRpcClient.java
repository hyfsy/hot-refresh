package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.remoting.client.DefaultRpcClient;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class NativeRpcClient extends RpcClientAdapter {

    public NativeRpcClient() {
        super(new DefaultRpcClient());
    }
}
