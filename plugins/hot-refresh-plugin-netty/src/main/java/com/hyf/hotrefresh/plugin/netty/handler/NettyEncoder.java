package com.hyf.hotrefresh.plugin.netty.handler;

import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byte[] encode = MessageCodec.encode(message);
        byteBuf.writeBytes(encode);
    }
}
