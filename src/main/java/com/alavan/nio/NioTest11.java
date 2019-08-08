package com.alavan.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Buffer 的 Scattering 和 Gathering
 * Scattering: 读时传递一个 buffer 数组, 向多个 buffer 中写入
 * @author Alavande
 */
public class NioTest11 {

    /**
     * 不要用 {@link Executors} 来创建线程池
     * 用 {@link ThreadPoolExecutor} 定义各种变量
     */
    private static Executor executor =
            new ThreadPoolExecutor(10, 20, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) throws IOException {

        // 使用 open 方法打开一个 serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 使用 8899 接收新客户端 socket 请求, 生成的 socket 会分配其他端口进行通信
        InetSocketAddress address = new InetSocketAddress(8899);
        // 绑定监听端口
        serverSocketChannel.socket().bind(address);

        int msgLength = 2 + 3 + 4;

        while (true) {
            // 建立的与客户端通信的 socket
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("与 " + socketChannel.getRemoteAddress() + " 建立连接......");
            executor.execute(() -> {

                ByteBuffer[] buffers = new ByteBuffer[3];
                buffers[0] = ByteBuffer.allocate(2);
                buffers[1] = ByteBuffer.allocate(3);
                buffers[2] = ByteBuffer.allocate(4);

                while (true) {
                    int byteRead = 0;
                    while (byteRead < msgLength && byteRead >= 0) {
                        long r = 0;
                        try {
                            r = socketChannel.read(buffers);
                            if (r < 0) {
                                System.out.println("与 " + socketChannel.getRemoteAddress()
                                        + " 连接断开, 结束此次对话......");
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byteRead += r;

                        System.out.println("byteRead: " + byteRead);

                        Arrays.asList(buffers).stream()
                                .map(buffer ->
                                        String.format("position: %s, limit: %s",
                                                buffer.position(), buffer.limit()))
                                .forEach(System.out::println);
                    }

                    Arrays.asList(buffers).forEach(buffer -> buffer.flip());

                    int byteWritten = 0;
                    while (byteWritten < msgLength && byteWritten >= 0) {
                        long r = 0;
                        try {
                            r = socketChannel.write(buffers);
                            if (r < 0) {
                                System.out.println("与 " + socketChannel.getRemoteAddress()
                                        + " 连接断开, 结束此次对话......");
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byteWritten += r;
                    }

                    Arrays.asList(buffers).forEach(byteBuffer -> byteBuffer.clear());

                    System.out.println(String.format("byteRead: %s, byteWritten: %s, msgLength: %s",
                            byteRead, byteWritten, msgLength));
                }
            });
        }
    }
}
