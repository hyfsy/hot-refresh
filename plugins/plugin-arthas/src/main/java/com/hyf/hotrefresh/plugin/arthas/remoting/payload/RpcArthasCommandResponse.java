package com.hyf.hotrefresh.plugin.arthas.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

public class RpcArthasCommandResponse extends RpcRequest {
    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_COMMAND_ARTHAS;
    }
}
