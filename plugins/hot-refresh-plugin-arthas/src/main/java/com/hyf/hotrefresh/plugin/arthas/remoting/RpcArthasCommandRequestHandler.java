package com.hyf.hotrefresh.plugin.arthas.remoting;

import com.hyf.hotrefresh.plugin.arthas.remoting.payload.RpcArthasCommandRequest;
import com.hyf.hotrefresh.plugin.arthas.remoting.payload.RpcArthasCommandResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;

public class RpcArthasCommandRequestHandler implements RpcMessageHandler<RpcArthasCommandRequest, RpcArthasCommandResponse> {
    @Override
    public RpcArthasCommandResponse handle(RpcArthasCommandRequest rpcMessage) throws Exception {
        return null;
    }
}
