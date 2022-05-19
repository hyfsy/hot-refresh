package com.hyf.hotrefresh.core.remoting.handler;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.refresh.HotRefresher;

import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class RpcHotRefreshRequestHandler implements RpcMessageHandler<RpcHotRefreshRequest, RpcHotRefreshResponse> {

    @Override
    public RpcHotRefreshResponse handle(RpcHotRefreshRequest request) throws Exception {

        Map<String, Object> headers = request.getHeaders();
        String fileName = request.getFileName();
        String fileLocation = request.getFileLocation();
        RpcRequestInst inst = request.getInst();

        try (InputStream content = request.getBody() /* 需要手动关闭 */) {

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
        }

        return new RpcHotRefreshResponse();
    }
}
