package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcErrorResponse extends RpcResponse {

    private Throwable t;

    public RpcErrorResponse() {
        setStatus(RemotingConstants.RESPONSE_ERROR);
        setData("Something error".getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_ERROR;
    }

    public Throwable getThrowable() {
        return t;
    }

    public void setThrowable(Throwable t) {
        this.t = t;
        resetWithThrowable();
    }

    private void resetWithThrowable() {
        setStatus(RemotingConstants.RESPONSE_ERROR);
        // 会序列化javac相关的类，无法被序列化导致报错
        // setData(MessageCodec.encodeObject(t, RpcMessageConstants.DEFAULT_ENCODING, RpcMessageConstants.DEFAULT_CODEC));
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(RemotingConstants.EXTRA_EXCEPTION_NESTED, ExceptionUtils.getNestedMessage(this.t));
        extraMap.put(RemotingConstants.EXTRA_EXCEPTION_STACK, ExceptionUtils.getStackMessage(this.t));
        setExtra(extraMap);
    }
}
