package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.remoting.rpc.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcSuccessResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;

import java.io.InputStream;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcRequestHandler implements RpcMessageHandler<RpcRequest, RpcResponse> {

    @Override
    public Class<RpcRequest> getRpcMessageClassType() {
        return RpcRequest.class;
    }

    @Override
    public RpcRequest createEmptyRpcMessage() {
        return new RpcRequest();
    }

    @Override
    public RpcResponse handle(RpcRequest request) throws Exception {

        Map<String, Object> headers = request.getHeaders();
        String fileName = request.getFileName();
        String fileLocation = request.getFileLocation();
        InputStream content = request.getContent();
        RpcRequestInst inst = request.getInst();

        // reset class
        if ("1".equals(headers.get("reset"))) {
            HotRefresher.reset();
        }

        String contentString = IOUtils.readAsString(content);
        if (contentString != null && !"".equals(contentString.trim())) {
            HotRefresher.refresh(fileName, contentString, ChangeType.valueOf(inst.name()).name());
        }

        return new RpcSuccessResponse();
    }
}
