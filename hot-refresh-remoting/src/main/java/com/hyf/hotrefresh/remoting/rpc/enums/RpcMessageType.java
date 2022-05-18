package com.hyf.hotrefresh.remoting.rpc.enums;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcMessageType {

    public static final byte REQUEST_BASIC       = 1;
    public static final byte RESPONSE_BASIC      = 2;
    public static final byte REQUEST_BATCH       = 3;
    public static final byte RESPONSE_BATCH      = 4;
    public static final byte REQUEST_HEARTBEAT   = 5;
    public static final byte RESPONSE_HEARTBEAT  = 6;
    public static final byte RESPONSE_SUCCESS    = 7;
    public static final byte RESPONSE_ERROR      = 8;
    public static final byte REQUEST_EXECUTABLE  = 9;
    public static final byte RESPONSE_EXECUTABLE = 10;

}
