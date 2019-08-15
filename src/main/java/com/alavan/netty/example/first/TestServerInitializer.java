package com.alavan.netty.example.first;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author Alavande
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        // 一个 pipeline 中会有很多个 handler 处理各自的业务请求
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 添加编解码器, 是 HttpRequestDecoder 和 HttpResponseEncoder 的合体
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
    }
}
