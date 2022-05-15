package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcResponseInst;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcRequestHandler implements RpcMessageHandler {

    @Override
    public Class<?> getRpcMessageClassType() {
        return RpcRequest.class;
    }

    @Override
    public RpcMessage handle(RpcMessage rpcMessage) throws Exception {

        RpcRequest request = (RpcRequest) rpcMessage;
        Map<String, Object> headers = request.getHeaders();
        String fileName = request.getFileName();
        String fileLocation = request.getFileLocation();
        InputStream content = request.getContent();
        RpcRequestInst inst = request.getInst();

        Throwable t = null;
        try {

            // reset class
            if ("1".equals(headers.get("reset"))) {
                HotRefresher.reset();
            }

            String contentString = IOUtils.readAsString(content);
            if (contentString != null && !"".equals(contentString.trim())) {
                HotRefresher.refresh(fileName, contentString, ChangeType.valueOf(inst.name()).name());
            }
        } catch (IOException | RefreshException e) {
            t = e;
        }

        return createResponse(t);
    }

    protected RpcMessage createResponse(Throwable t) {

        RpcResponse response = new RpcResponse();

        // success
        if (t == null) {
            response.setStatus(200);
        }
        // error
        else {
            String sb = ExceptionUtils.getNestedMessage(t) + Constants.MESSAGE_SEPARATOR + ExceptionUtils.getStackMessage(t);
            response.setStatus(500);
            response.setInst(RpcResponseInst.LOG);
            response.setData(sb.getBytes(Constants.MESSAGE_ENCODING));
        }

        return response;
    }
}
