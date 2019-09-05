package com.alavan.netty.example.second.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Alavande
 */
public class MyServer {

    public static void main(String[] args) throws InterruptedException {

        // EventLoopGroup (I) 是一个特殊的 EventExecutorGroup
        // 在 EventLoopGroup 中可以注册 Channel
        // 被注册的 Channel 在循环中会被之后的 Selection 操作所使用
        //
        // EventLoopGroup 中包含了很多的 EventLoop
        // 在 NioEventLoopGroup 的初始化中, 完成了很多默认的赋值工作
        // 很多时候会使用 new NioEventLoopGroup(1) 用以创建只有一个线程的线程池
        // 因为 bossGroup 只需要执行获取分配连接一个操作
        //
        // 真正的初始化在 MultithreadEventExecutorGroup 中进行
        // Executor 为 ThreadPerTaskExecutor
        // 默认使用的 EventExecutorChooserFactory 采用轮询来获取下一个事件, next() 方法
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{

            // ServerBootstrap 用于启动 ServerChannel, ServerChannel 是一个标记接口, 没有内容
            // 使用 NoArgConstructor
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // group() 方法仅给成员变量赋值,
                    // bossGroup 赋值给 ServerBootstrap 父类  AbstractBootstrap 中的 group
                    // workerGroup 赋值给 ServerBootstrap 中的 childGroup
                    .group(bossGroup, workerGroup)
                    // channel() 方法仅仅 new 一个 channelFactory 实例, 赋值给 AbstractBootstrap 中的 channelFactory
                    // 由 channelFactory 将在调用 bind() 方法时创建 channel 实例
                    .channel(NioServerSocketChannel.class)
                    // 给 ServerBootstrap 中的 childHandler 赋值, 传入的 handler 用于处理针对 channel 的请求
                    .childHandler(new MyServerInitializer());

            ChannelFuture channelFuture = serverBootstrap
                    // 创建一个新的 channel 并绑定到端口, 最终调用 AbstractBootstrap 中的 doBind() 方法
                    // doBind() 中:
                    //   1. initAndRegister():
                    //     1.1 channelFactory.newChannel() 调用了实现类 ReflectiveChannelFactory 的 newChannel() 方法
                    //         通过反射, 使用传入的 channel 的 class (NioServerSocketChannel) 获取<无参>构造器新建了一个 channel 实例
                    //     1.2 init(Channel) 调用了 ServerBootstrap 中的 init(Channel) 方法
                    .bind(8899)
                    // 等待直到 future 结束返回
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
