package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author baB_hyf
 * @date 2022/09/02
 */
public class SocketChannelContext {

    private static final Map<SocketChannel, Lock> channelTable = new ConcurrentHashMap<>();

    private final    SocketChannel       socketChannel;
    /* @Nullable */
    private volatile ByteBuffer          rcvBuf;
    /* @Nullable */
    private volatile ByteBuffer          sndBuf;
    /* @Nullable */
    private volatile Map<String, Object> attributeMap;

    private volatile boolean readComplete  = false;
    private volatile boolean writeComplete = false;

    public SocketChannelContext(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Lock getReadLock() {
        clearClosedChannel();
        channelTable.computeIfAbsent(this.socketChannel, sc -> new ReentrantLock());
        return channelTable.get(this.socketChannel);
    }

    public Lock getWriteLock() {
        clearClosedChannel();
        channelTable.computeIfAbsent(this.socketChannel, sc -> new ReentrantLock());
        return channelTable.get(this.socketChannel);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public ByteBuffer getRcvBuf() {
        return rcvBuf;
    }

    public void setRcvBuf(ByteBuffer rcvBuf) {
        this.rcvBuf = rcvBuf;
    }

    public ByteBuffer getSndBuf() {
        return sndBuf;
    }

    public void setSndBuf(ByteBuffer sndBuf) {
        this.sndBuf = sndBuf;
    }

    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(Map<String, Object> attributeMap) {
        this.attributeMap = attributeMap;
    }

    public boolean isReadComplete() {
        return readComplete;
    }

    public void setReadComplete(boolean readComplete) {
        this.readComplete = readComplete;
    }

    public boolean isWriteComplete() {
        return writeComplete;
    }

    public void setWriteComplete(boolean writeComplete) {
        this.writeComplete = writeComplete;
    }

    public void reset() {
        this.readComplete = false;
        this.writeComplete = false;
    }

    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            if (Log.isDebugMode()) {
                Log.error("Failed to close socket channel: " + socketChannel, e);
            }
        }
    }

    private void clearClosedChannel() {
        channelTable.forEach((c, l) -> {
            if (c.socket().isClosed()) {
                channelTable.remove(c);
            }
        });
    }
}
