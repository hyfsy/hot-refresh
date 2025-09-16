package com.hyf.hotrefresh.core.remoting.handler;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.core.remoting.payload.*;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 批处理，有些能成功，但某个类导致后面transform全失败的情况，效率反而不高。。。
 *
 * @author baB_hyf
 * @date 2022/05/19
 */
public class RpcHotRefreshBatchRequestHandler implements RpcMessageHandler<RpcHotRefreshBatchRequest, RpcHotRefreshBatchResponse> {

    @Override
    public RpcHotRefreshBatchResponse handle(RpcHotRefreshBatchRequest request) throws Exception {

        Map<String, Object> headers = request.getHeaders();
        List<RpcHotRefreshRequest> requests = request.getRequests();

        List<RpcHotRefreshResponse> responses = new ArrayList<>();

        for (RpcHotRefreshRequest req : requests) {
            RpcHotRefreshRequestInst inst = req.getInst();
            if (inst == RpcHotRefreshRequestInst.UNKNOWN) {
                RpcHotRefreshResponse response = new RpcHotRefreshResponse();
                response.setStatus(RpcResponse.ERROR);
                response.setData("Request inst unknown".getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
                responses.add(response);
                continue;
            }
        }

        List<HotRefresher.RefreshUnit> refreshUnits = new ArrayList<>();
        for (RpcHotRefreshRequest req : requests) {

            String fileName = req.getFileName();
            String fileLocation = req.getFileLocation();
            RpcHotRefreshRequestInst inst = req.getInst();

            try (InputStream content = request.getBody() /* 需要手动关闭 */) {

                // 热刷新
                if (Arrays.asList(RpcHotRefreshRequestInst.CREATE, RpcHotRefreshRequestInst.MODIFY, RpcHotRefreshRequestInst.DELETE).contains(inst)) {
                    byte[] contentBytes = IOUtils.readAsByteArray(content);
                    if (contentBytes.length > 0) {
                        refreshUnits.add(new HotRefresher.RefreshUnit(fileName, contentBytes, ChangeType.valueOf(inst.name()).name()));
                    }
                }
            } catch (Throwable t) {
            }
        }

        try {
            HotRefresher.batchRefresh(refreshUnits);
        } catch (RefreshException e) {
            throw new RuntimeException(e);
        }

        return new RpcHotRefreshBatchResponse();
    }
}
