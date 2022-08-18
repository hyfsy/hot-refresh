package com.hyf.hotrefresh.remoting.constants;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCompression;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RemotingConstants {

    // 1: formulate specification(1.1+)
    // 2: slimming message, e.g. remove empty map(1.3+)
    public static final byte MESSAGE_VERSION = 2;

    public static final String DEFAULT_CONTENT_TYPE = "application-hot-refresh/hex-stream";

    public static final RpcMessageEncoding DEFAULT_ENCODING = RpcMessageEncoding.getEncoding(Constants.MESSAGE_ENCODING);

    public static final RpcMessageCodec DEFAULT_CODEC = RpcMessageCodec.JDK;

    public static final RpcMessageCompression DEFAULT_COMPRESSION = RpcMessageCompression.GZIP;

    public static final int RESPONSE_UNKNOWN = -1;
    public static final int RESPONSE_SUCCESS = 200;
    public static final int RESPONSE_ERROR   = 500;

    public static final String EXTRA_EXCEPTION_NESTED = "nestedExceptionMessage";
    public static final String EXTRA_EXCEPTION_STACK  = "stackExceptionMessage";

}
