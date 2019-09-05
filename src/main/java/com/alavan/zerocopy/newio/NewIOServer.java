package com.alavan.zerocopy.newio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Alavande
 */
public class NewIOServer {

    public static void main(String[] args) throws IOException {
        InetSocketAddress address = new InetSocketAddress(8899);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 一个 TCP 连接被关闭时, 连接还会以超时(TIMEOUT_STATE)的状态保持一定时间
        // 这个状态通常被成为 TIME-WAIT 状态
        // 当应用关闭连接后想重新使用这个 地址 和 端口 时, 其还是被占用着并不能直接重用
        // 当在使用 bind() 方法前, 启动这个配置, 就可以直接重用 TIME-WAIT 状态下的端口和地址
        serverSocket.setReuseAddress(true);
        serverSocket.bind(address);

        ByteBuffer buffer = ByteBuffer.allocate(4096);

        while (true) {
            // accept() 方法返回的 socketChannel 一定是阻塞模式的
            // 无论当前的 serverSocketChannel 是什么模式
            SocketChannel socketChannel = serverSocketChannel.accept();

            int count = 0;

            while (-1 != count) {
                try {
                    count = socketChannel.read(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                // position = 0, mark = -1
                buffer.rewind();
//                buffer.clear();
            }
        }
    }
}
