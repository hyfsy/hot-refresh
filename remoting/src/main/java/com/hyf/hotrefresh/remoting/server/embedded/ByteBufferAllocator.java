package com.hyf.hotrefresh.remoting.server.embedded;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * @author baB_hyf
 * @date 2022/09/03
 */
public class ByteBufferAllocator {

    private static final int bufferInitCapacity = 2048;

    private static final ByteBufferAllocator allocator = new ByteBufferAllocator();

    private ByteBufferAllocator() {
    }

    public static ByteBufferAllocator getInstance() {
        return allocator;
    }

    public ByteBuffer alloc() {
        return alloc(bufferInitCapacity);
    }

    public ByteBuffer alloc(int initCapacity) {
        return ByteBuffer.allocate(initCapacity);
    }

    public ByteBuffer realloc(ByteBuffer buf) {
        return realloc(buf, 0);
    }

    public ByteBuffer realloc(ByteBuffer buf, int truncateSize) {
        if (buf == null) {
            return ByteBuffer.allocate(bufferInitCapacity);
        }

        if (buf.capacity() == Integer.MAX_VALUE) {
            throw new BufferOverflowException();
        }

        if (truncateSize > buf.position()) {
            throw new IllegalArgumentException("truncateSize greater than exist bytes count");
        }

        long l = buf.capacity();
        if (buf.position() - truncateSize > buf.capacity() / 2) { // 截断后小于原来的一半，则不扩容
            l = buf.capacity() * 2L;
        }

        if (l > Integer.MAX_VALUE) {
            l = Integer.MAX_VALUE;
        }
        ByteBuffer newBuf = ByteBuffer.allocate((int) (l));
        buf.flip();
        buf.position(truncateSize);
        newBuf.put(buf);
        return newBuf;
    }
}
