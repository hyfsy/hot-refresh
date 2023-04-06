package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.client.api.client.RpcClientAdapter;
import com.hyf.hotrefresh.remoting.client.DefaultRpcClient;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class NativeSocketClient extends RpcClientAdapter {

    public NativeSocketClient() {
        super(new DefaultRpcClient());
    }
}
