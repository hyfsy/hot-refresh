package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.RpcSuccessResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcRequestHandler implements RpcMessageHandler<RpcRequest, RpcResponse> {

    @Override
    public RpcResponse handle(RpcRequest request) throws Exception {

        try {
            Map<String, Object> headers = request.getHeaders();
            String fileName = request.getFileName();
            String fileLocation = request.getFileLocation();
            InputStream content = request.getContent(); // 需要手动关闭
            RpcRequestInst inst = request.getInst();

            // 热刷新
            if (Arrays.asList(RpcRequestInst.CREATE, RpcRequestInst.MODIFY, RpcRequestInst.DELETE).contains(inst)) {
                // reset class
                if ("1".equals(headers.get("reset"))) {
                    HotRefresher.reset();
                }

                String contentString = IOUtils.readAsString(content);
                if (contentString != null && !"".equals(contentString.trim())) {
                    HotRefresher.refresh(fileName, contentString, ChangeType.valueOf(inst.name()).name());
                }
            }
        } finally {
            InputStream content = request.getContent();
            if (content != null) {
                content.close();
            }
        }

        return new RpcSuccessResponse();
    }
}
