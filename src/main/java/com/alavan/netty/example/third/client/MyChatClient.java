package com.alavan.netty.example.third.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * @author Alavande
 */
public class MyChatClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MyChatClientInitializer());

            Channel channel = bootstrap.connect("localhost", 8899).sync().channel();

            // 开始循环, 不断读取用户在控制台上输入的内容
            Scanner scanner = new Scanner(System.in);
            while (true) {
                channel.writeAndFlush(scanner.nextLine() + "\n");
            }

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
