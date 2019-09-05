package com.alavan.netty.example.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty 测试服务器
 * @author Alavande
 */
public class TestServer {

    public static void main(String[] args) throws InterruptedException {

        // EventLoopGroup 是一个死循环
        // bossGroup 主要用来接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 服务器启动引导类, 简化 netty 服务器创建
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // handler 是对与 bossGroup 的处理器
//                    .handler()
                    // childHandler 是对 workGroup 的处理器
                    // 真正连接到来的处理方法
                    .childHandler(new TestServerInitializer());
            ChannelFuture channelFuture = serverBootstrap
                    // 绑定接口
                    .bind(8899)
                    // 阻塞, 同步等待客户端连接
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            // netty 提供的优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
