package com.alavan.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Alavande
 */
public class NioTest12 {

    public static void main(String[] args) throws IOException {
        int[] ports = new int[5];

        // 直到调用 close() 方法前都不会关闭
        // 在 Selector 中, SelectionKey 代表了可选择的 channel 集合
        // Selector 维护三种 selection key 集合
        // keys() 方法返回现在注册的所有 channel 的集合
        //
        // cancelled-key 集合包含了所有 key 已经取消但是对应 channel 还没解绑的 key
        //
        // 通过 channel 的 register() 方法将 channel 注册到 selector 时, 都会将一个 key 加入 selector 的 key set
        Selector selector = Selector.open();

        for (int i = 0; i < 5; i++) {
            ports[i] = 5000 + i;

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 调整 channel 的阻塞模式
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            serverSocket.bind(address);

            // 将 serverSocketChannel 注册到 selector 中, 可以通过对应的 selectionKey 获取
            // serverSocketChannel 监听连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("监听端口: " + ports[i]);
        }

        while (true) {
            // select() 方法是阻塞的, 直到至少一个 channel 被选择
            // 返回一组 keys, 其对应的 channels 以准备好 I/O 操作
            int nums = selector.select();
            System.out.println(nums + " 个 channels 等待 I/O 操作......");

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // 遍历 keys 和其对应的等待操作的 channel
            while (iterator.hasNext()) {
                // 获取迭代器中下一个 selectionKey
                 SelectionKey selectionKey = iterator.next();
                System.out.println("获取 selectionKey: " + selectionKey);

                 // 建立连接的请求
                 if (selectionKey.isAcceptable()) {
                     System.out.println("收到连接请求......");

                     // 直接通过 key 获取对应的 channel, 建立连接应该获取 serverSocketChannel
                     ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                     SocketChannel socketChannel = serverSocketChannel.accept();
                     // 配置非阻塞模式
                     socketChannel.configureBlocking(false);

                     // 将与客户端建立的通信 socketChannel 注册到 selector 上并监听读操作
                     socketChannel.register(selector, SelectionKey.OP_READ);

                     System.out.println("与客户端建立连接: " + socketChannel);
                 } else if (selectionKey.isReadable()) {
                     System.out.println("开始读请求......");
                     // 读请求, 获取对应的 socketChannel
                     SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                     int byteRead = 0;
                     ByteBuffer buffer = ByteBuffer.allocate(1024);

                     // 持续读连接
                     while (true) {
                         int r = socketChannel.read(buffer);

                         if (r <= 0) {
                             // 断开连接, 跳出循环
                             System.out.println("客户端断开连接......");
                             break;
                         }

                         buffer.flip();
                         socketChannel.write(buffer);

                         byteRead += r;
                         System.out.println("读取: " + byteRead + ", 来自于: " + socketChannel);
                     }
                 }

                // 从当前迭代器中移除已经完成操作的 selectionKey
                System.out.println("移除当前 selectionKey: " + selectionKey);
                iterator.remove();
            }
        }
    }
}
