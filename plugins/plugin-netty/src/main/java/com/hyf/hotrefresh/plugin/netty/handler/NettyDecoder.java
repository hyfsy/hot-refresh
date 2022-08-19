package com.hyf.hotrefresh.plugin.netty.handler;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.plugin.netty.client.ConnectionManager;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;


/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    // 16MB
    private static final int MAX_FRAME_LENGTH =
            Integer.parseInt(System.getProperty(Constants.PROPERTIES_PREFIX + ".frameMaxLength", "16777216"));

    public NettyDecoder() {
        // maxFrameLength       - 最大报文长度
        // lengthFieldOffset    - 长度字段从第几个字节开始(0-)
        // lengthFieldLength    - 长度字段的字节数
        // lengthAdjustment     - 长度包括的范围，0表示从长度字节后开始，负数表示前面的字节也包含在长度内
        // initialBytesToStrip  - 解码移除的字节数
        super(MAX_FRAME_LENGTH, MessageCodec.MAGIC.length + 1, 4, -(MessageCodec.MAGIC.length + 1 + 4), 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decoded = null;
        try {
            decoded = (ByteBuf) super.decode(ctx, in); // null is false
            if (decoded == null) {
                return null;
            }

            if (decoded.isDirect()) {
                ByteBuffer byteBuffer = decoded.nioBuffer();
                return MessageCodec.decode(byteBuffer);
            }
            else {
                return MessageCodec.decode(decoded.array());
            }
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.error("Decode failed", e);
            }
            ConnectionManager.closeChannel(ctx.channel());
        } finally {
            if (decoded != null) {
                decoded.release();
            }
        }
        return decoded;
    }
}
