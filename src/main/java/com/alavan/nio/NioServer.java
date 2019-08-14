package com.alavan.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

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

//        Map<String, SocketChannel> clientMap = new HashMap<>();
        Set<SocketChannel> clientSet = new HashSet<>();

        while (true) {
            int nums = selector.select();
            System.out.println("发生 " + nums + " 事件......");
            // 获取事件集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 遍历所有发生的事件
            // 当处理完 selectionKey 时一定要从集合中移除, 否则 selectionKey 会被重复处理
            selectionKeys.forEach(selectionKey -> {
                final SocketChannel client;
                try {
                    // 连接事件
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        client = server.accept();
                        System.out.println(client + " 已连接......");
                        // 非阻塞
                        client.configureBlocking(false);
                        // 客户端在 selector 上注册读请求事件
                        client.register(selector, SelectionKey.OP_READ);
                        // 存入 map 中便于查找
                        clientSet.add(client);
                    } else if (selectionKey.isReadable()) {
                        // 读事件
                        client = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        new Thread(() -> {
                            while (true) {
                                try {
                                    int count = client.read(buffer);
                                    if (count > 0) {
                                        buffer.flip();
                                        Charset charset = Charset.forName("utf-8");
                                        String msg = String.valueOf(charset.decode(buffer).array());
                                        System.out.println(client + ": " + msg);
                                        buffer.clear();

                                        for (SocketChannel sc : clientSet) {
                                            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                                            if (sc != client) {
                                                writeBuffer.put((client.getRemoteAddress() + ": " + msg).getBytes());
                                            } else {
                                                writeBuffer.put(("收到信息: " + msg).getBytes());
                                            }
                                            writeBuffer.flip();
                                            sc.write(writeBuffer);
                                        }
                                    } else if (count < 0) {
                                        System.out.println(client + " 断开连接......");
                                        clientSet.remove(client);
                                        client.close();
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            selectionKeys.clear();
        }
    }
}
