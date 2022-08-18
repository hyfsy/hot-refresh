package com.hyf.hotrefresh.plugin.netty.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.plugin.netty.server.NettyRpcServer;
import com.hyf.hotrefresh.remoting.exception.RpcException;
import com.hyf.hotrefresh.remoting.message.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    private final NettyRpcServer hotRefreshServer;

    public ServerHandler(NettyRpcServer hotRefreshServer) {
        this.hotRefreshServer = hotRefreshServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        try {
            Message response = hotRefreshServer.handle(message);
            if (response != null) {
                ctx.writeAndFlush(response);
            }
        } catch (RpcException e) {
            if (Log.isDebugMode()) {
                Log.error("Failed to process request", e);
            }
        }
    }
}
