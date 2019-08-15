package com.alavan.netty.example.fourth.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Alavande
 */
public class MyChatServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 触发一个事件的时候
     * 此方法会将触发的事件传递给管道中
     * 下一个 handler 进行处理
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            // 强制类型转换
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String eventType = null;

            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }

            System.out.println(ctx.channel().remoteAddress() + " 超时状态: " + eventType);
            ctx.channel().close();
        }
    }
}
