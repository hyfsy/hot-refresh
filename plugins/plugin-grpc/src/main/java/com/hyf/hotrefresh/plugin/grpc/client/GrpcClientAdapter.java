package com.hyf.hotrefresh.plugin.grpc.client;

import com.hyf.hotrefresh.client.core.client.RpcClientAdapter;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class GrpcClientAdapter extends RpcClientAdapter {

    public GrpcClientAdapter() {
        super(new GrpcClient());
    }
}
