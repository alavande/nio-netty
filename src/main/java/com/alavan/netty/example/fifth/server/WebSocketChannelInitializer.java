package com.alavan.netty.example.fifth.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Alavande
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        // netty 内置
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        // netty 会将 request 请求分成多个 1000 一下的段
        // 每个段都会走一遍完整的 read0 流程
        // 使用这个 handler 以后会把多个段 HttpObject 进行聚合
        // 根据传入值的不同, 会形成一个 FullHttpRequest 或者 FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(8192));
        // 用于处理 WebSocket 握手以及 6 种 Frame 的请求
        // Byte, Continue, Ping, Pong, Close, Text
        // websocketPath: ws://server:port/context_path, 有自己的协议格式
        // "/ws" 指定的就是 context_path
        // Websocket 是 http 的升级连接, 通过一个 http 连接的升级获得
        // 客户端和服务器间将获得一个全双工的连接
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        pipeline.addLast(new TextWebsocketFrameHandler());
    }
}
