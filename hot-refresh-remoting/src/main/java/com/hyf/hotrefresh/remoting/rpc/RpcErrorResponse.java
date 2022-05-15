package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.remoting.constants.RpcMessageConstants;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcResponseInst;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcErrorResponse extends RpcResponse {

    public static final String NESTED_MESSAGE = "nestedMessage";
    public static final String STACK_MESSAGE  = "stackMessage";

    private Throwable t;

    public RpcErrorResponse() {
        setStatus(500);
        setInst(RpcResponseInst.LOG);
        setData("Something error".getBytes(RpcMessageConstants.DEFAULT_ENCODING.getCharset()));
    }

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.ERROR_RESPONSE;
    }

    public Throwable getThrowable() {
        return t;
    }

    public void setThrowable(Throwable t) {
        this.t = t;
        resetWithThrowable();
    }

    private void resetWithThrowable() {
        setStatus(500);
        setInst(RpcResponseInst.LOG);
        // 会序列化javac相关的类，无法被序列化导致报错
        // setData(MessageCodec.encodeObject(t, RpcMessageConstants.DEFAULT_ENCODING, RpcMessageConstants.DEFAULT_CODEC));
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(NESTED_MESSAGE, ExceptionUtils.getNestedMessage(t));
        extraMap.put(STACK_MESSAGE, ExceptionUtils.getStackMessage(t));
        setExtra(extraMap);
    }
}
