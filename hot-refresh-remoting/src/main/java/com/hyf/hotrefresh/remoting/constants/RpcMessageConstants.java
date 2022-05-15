package com.hyf.hotrefresh.remoting.constants;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCompression;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcMessageConstants {

    public static final String DEFAULT_CONTENT_TYPE = "application-hot-refresh/hex-stream";

    public static final RpcMessageEncoding DEFAULT_ENCODING = RpcMessageEncoding.getEncoding(Constants.MESSAGE_ENCODING);

    public static final RpcMessageCodec DEFAULT_CODEC = RpcMessageCodec.JDK;

    public static final RpcMessageCompression DEFAULT_COMPRESSION = RpcMessageCompression.GZIP;

}
