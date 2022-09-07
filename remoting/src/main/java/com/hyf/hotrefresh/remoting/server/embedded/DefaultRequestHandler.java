package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ByteUtils;
import com.hyf.hotrefresh.remoting.exception.CodecException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig.TCP_DEBUG;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
public class DefaultRequestHandler implements RequestHandler {

    private static final int maxFrameLength      = 16777216;
    private static final int lengthFieldOffset   = MessageCodec.MAGIC.length + 1;
    private static final int lengthFieldLength   = 4;
    private static final int lengthAdjustment    = -(MessageCodec.MAGIC.length + 1 + 4);
    private static final int initialBytesToStrip = 0;

    private static final ByteBufferAllocator allocator = ByteBufferAllocator.getInstance();

    private final EmbeddedRpcServer embeddedRpcServer;

    public DefaultRequestHandler(EmbeddedRpcServer embeddedRpcServer) {
        this.embeddedRpcServer = embeddedRpcServer;
    }

    @Override
    public ByteBuffer read(SocketChannelContext scc) throws IOException {
        if (TCP_DEBUG) {
            Log.info("read");
        }

        ByteBuffer buf = getAdaptiveRcvBuffer(scc);

        // -1是约定的中止值,如果通道在该selector的位值包含有read事件，并且被客户端中断了，
        // 那么selector会无限通知我们有可读事件，其实读出来的是中止值-1
        int read = scc.getSocketChannel().read(buf);
        if (read == -1) {
            scc.setReadComplete(true);
            if (TCP_DEBUG) {
                Log.info("read -1");
            }
            return null;
        }

        if (buf.position() > lengthFieldOffset + lengthFieldLength) { // 接收到长度字段字节

            int messageLength = getMessageLength(buf);
            int packageLength = lengthFieldOffset + lengthFieldLength + messageLength + lengthAdjustment;
            if (packageLength > maxFrameLength) {
                throw new CodecException("Message too long: " + packageLength + ", limit: " + maxFrameLength);
            }

            if (buf.position() >= packageLength) { // 数据包完整接收
                ByteBuffer newBuf = allocator.alloc(packageLength);
                newBuf.put(buf.array(), 0, packageLength); // TODO 直接缓存
                newBuf.position(initialBytesToStrip);
                scc.setRcvBuf(allocator.realloc(buf, packageLength));
                return newBuf;
            }
        }

        if (TCP_DEBUG) {
            Log.info("read null");
        }

        return null;
    }

    @Override
    public ByteBuffer handle(SocketChannelContext scc, ByteBuffer buf) throws Throwable {
        if (TCP_DEBUG) {
            Log.info("handle");
        }

        if (buf == null) {
            return null;
        }

        Message request = MessageCodec.decode(buf);
        Message response = embeddedRpcServer.handle(request);
        return MessageCodec.encodeByteBuffer(response);
    }

    @Override
    public void write(SocketChannelContext scc, ByteBuffer buf) throws IOException {
        if (TCP_DEBUG) {
            Log.info("write");
        }

        if (buf == null) {
            scc.setWriteComplete(true);
            return;
        }

        int write;
        while ((write = scc.getSocketChannel().write(buf)) > 0) {
        }
        if (write == -1) {
            scc.setWriteComplete(true);
            if (TCP_DEBUG) {
                Log.info("write -1");
            }
        }
    }

    @Override
    public void caught(SocketChannelContext scc, ByteBuffer request, ByteBuffer response, Throwable t) {
        if (TCP_DEBUG) {
            Log.info("caught");
        }

        try {
            if (response != null) {
                if (Log.isDebugMode()) {
                    Log.error("Failed to write channel: " + ByteUtils.toString(response.array()), t); // TODO 直接缓存
                }
            }
            else if (request != null) {
                ByteBuffer buf = request.asReadOnlyBuffer();
                buf.reset();
                RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
                rpcErrorResponse.setThrowable(t);
                Message responseMessage = MessageFactory.createResponseMessage(MessageCodec.decode(buf), rpcErrorResponse);
                this.write(scc, MessageCodec.encodeByteBuffer(responseMessage));
            }
            else {
                if (Log.isDebugMode()) {
                    Log.error("Failed to read channel", t);
                }
            }
        } catch (IOException e) {
            if (Log.isDebugMode()) {
                Log.error("Failed to handler exception", e);
            }
        }
    }

    private ByteBuffer getAdaptiveRcvBuffer(SocketChannelContext scc) {
        ByteBuffer rcvBuf = scc.getRcvBuf();
        if (rcvBuf == null) {
            synchronized (scc) {
                rcvBuf = scc.getRcvBuf();
                if (rcvBuf == null) {
                    rcvBuf = allocator.alloc();
                    scc.setRcvBuf(rcvBuf);
                }
            }
        }
        if (!rcvBuf.hasRemaining()) {
            synchronized (scc) {
                if (!rcvBuf.hasRemaining()) {
                    rcvBuf = allocator.realloc(rcvBuf);
                    scc.setRcvBuf(rcvBuf);
                }
            }
        }
        return rcvBuf;
    }

    private int getMessageLength(ByteBuffer buf) {
        ByteBuffer bufferView = buf.asReadOnlyBuffer();
        bufferView.position(lengthFieldOffset);
        int messageLength;
        switch (lengthFieldLength) {
            case 1:
                messageLength = bufferView.get();
                break;
            case 2:
                messageLength = bufferView.getChar();
                break;
            case 4:
                messageLength = bufferView.getInt();
                break;
            case 8:
                messageLength = (int) bufferView.getLong();
                break;
            default:
                throw new IllegalArgumentException("Illegal lengthFieldLength: " + lengthFieldLength);
        }
        return messageLength;
    }
}
