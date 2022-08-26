package com.hyf.hotrefresh.remoting.server.embedded;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
interface RequestHandler {

    ByteBuffer read(SocketChannel sc) throws IOException;

    ByteBuffer handle(ByteBuffer buf) throws Throwable;

    void write(SocketChannel sc, ByteBuffer buf) throws IOException;

    void caught(SocketChannel sc, /* @Nullable */ ByteBuffer buf, Throwable t);
}
