package com.hyf.hotrefresh.remoting.rpc.enums;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcMessageType {

    /**
     * @deprecated this request type has not way to handler, just a placeholder,
     * please declare specific message type to use
     */
    public static final byte REQUEST_BASIC        = 1;
    public static final byte RESPONSE_BASIC       = 2;
    public static final byte REQUEST_BATCH        = 3;
    public static final byte RESPONSE_BATCH       = 4;
    public static final byte REQUEST_HEARTBEAT    = 5;
    public static final byte RESPONSE_HEARTBEAT   = 6;
    public static final byte RESPONSE_SUCCESS     = 7;
    public static final byte RESPONSE_ERROR       = 8;
    public static final byte REQUEST_HOT_REFRESH  = 9;
    public static final byte RESPONSE_HOT_REFRESH = 10;
    public static final byte REQUEST_EXECUTABLE   = 11;
    public static final byte RESPONSE_EXECUTABLE  = 12;

}
