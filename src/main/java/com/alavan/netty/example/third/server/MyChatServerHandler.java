package com.alavan.netty.example.third.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Alavande
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 保存 channel 对象
     */
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();

        channels.forEach(c -> {
            if (c != channel) {
                c.writeAndFlush(channel.remoteAddress() + ": " + s + "\n");
            } else {
                c.writeAndFlush("自己: " + s + "\n");
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // channelGroup 的 writeAndFlush 会向其中的所有 channel 遍历发送数据
        channels.writeAndFlush(channel.remoteAddress() + " 已经上线......\n");
        // 加入 channelGroup
        channels.add(channel);
    }

    /**
     * 连接断开的回调
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        channels.writeAndFlush(channel.remoteAddress() + " 已下线......\n");
        // 连接断开后 netty 会自动将 channel 从 channelGroup 中移除, 不需手动调用
//        channels.remove(channel);
        System.out.println("channels: " + channels.size());
    }

    /**
     * 连接处于活动状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 已经上线......\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 已下线......\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
