package com.alavan.netty.example.second.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @author Alavande
 */
public class MyServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 连接事件处理
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(channelHandlerContext.channel().remoteAddress() + ": " + s);

        channelHandlerContext.writeAndFlush("From server: " + UUID.randomUUID());
    }

    /**
     * 捕获异常后一般关闭连接
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("已接收连接......");
    }
}
