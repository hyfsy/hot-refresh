package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.common.Constants;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcMessageConstants {

    public static final String HTTP_CONTENT_TYPE = "application/octet-stream";

    public static final RpcMessageEncoding DEFAULT_ENCODING = RpcMessageEncoding.getEncoding(Constants.MESSAGE_ENCODING);

    public static final RpcMessageCodec DEFAULT_CODEC = RpcMessageCodec.JDK;

    public static final RpcMessageCompression DEFAULT_COMPRESSION = RpcMessageCompression.GZIP;

}
