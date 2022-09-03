package com.hyf.hotrefresh.remoting.server.embedded;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
interface RequestHandler {

    ByteBuffer read(SocketChannelContext scc) throws IOException;

    ByteBuffer handle(SocketChannelContext scc, /* @Nullable */ ByteBuffer request) throws Throwable;

    void write(SocketChannelContext scc, /* @Nullable */ ByteBuffer response) throws IOException;

    void caught(SocketChannelContext scc, /* @Nullable */ ByteBuffer request, /* @Nullable */ ByteBuffer response, Throwable t);

}
