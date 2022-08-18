package com.hyf.hotrefresh.plugin.netty.handler;

import com.hyf.hotrefresh.common.Log;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
@ChannelHandler.Sharable
public class ConnectionHandler extends ChannelDuplexHandler {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (Log.isDebugMode()) {
            Log.info("Channel " + ctx.channel() + " registered");
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (Log.isDebugMode()) {
            Log.info("Channel " + ctx.channel() + " unregistered");
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (Log.isDebugMode()) {
            Log.info("Channel " + ctx.channel() + " active");
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (Log.isDebugMode()) {
            Log.info("Channel " + ctx.channel() + " inactive");
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (Log.isDebugMode()) {
            Log.error("Channel " + ctx.channel() + " exceptionCaught", cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (Log.isDebugMode()) {
            Log.info(ctx + " will closed");
        }
        super.close(ctx, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                if (Log.isDebugMode()) {
                    Log.info("Channel " + ctx.channel() + " idled");
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
