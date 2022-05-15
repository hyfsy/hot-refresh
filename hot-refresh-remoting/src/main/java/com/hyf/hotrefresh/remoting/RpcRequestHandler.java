package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.refresh.HotRefresher;

import java.io.IOException;
import java.io.InputStream;

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
        String fileName = request.getFileName();
        String fileLocation = request.getFileLocation();
        InputStream content = request.getContent();
        RpcRequestInst inst = request.getInst();

        Throwable t = null;
        try {
            String contentString = com.hyf.hotrefresh.common.util.IOUtils.readAsString(content);
            if (contentString != null && !"".equals(contentString.trim())) {
                HotRefresher.refresh(fileName, contentString, ChangeType.valueOf(inst.name()).name());
            }
        } catch (IOException | RefreshException e) {
            t = e;
        }

        return createResponse(t);
    }

    private RpcMessage createResponse(Throwable t) {

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
