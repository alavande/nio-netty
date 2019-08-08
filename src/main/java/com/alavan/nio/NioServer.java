package com.alavan.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author Alavande
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        // 直接给对应的 serverSocket 绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8899));

        Selector selector = Selector.open();
        // 注册到 selector 上并监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
}
