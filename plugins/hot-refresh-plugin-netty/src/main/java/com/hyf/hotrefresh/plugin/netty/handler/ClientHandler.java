package com.hyf.hotrefresh.plugin.netty.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.exception.RpcException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.ResponseFuture;
import com.hyf.hotrefresh.plugin.netty.client.NettyRpcClient;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final NettyRpcClient nettyRpcClient;
    private final MessageHandler clientMessageHandler;

    public ClientHandler(NettyRpcClient nettyRpcClient) {
        this.nettyRpcClient = nettyRpcClient;
        this.clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        try {
            Message response = clientMessageHandler.handle(message);
            ResponseFuture responseFuture = nettyRpcClient.getFutureTables().get(message.getId());
            if (responseFuture != null) {
                responseFuture.success(response);
            }
        } catch (RpcException e) {
            Log.error("Failed to process request", e);
        }
    }
}
