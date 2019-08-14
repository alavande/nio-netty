package com.alavan.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Alavande
 */
public class NioClient {

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));

            while (true) {
                int nums = selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();

                for (SelectionKey selectionKey : selectionKeySet) {
                    if (selectionKey.isConnectable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        if (client.isConnectionPending()) {
                            client.finishConnect();
                        }

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put((client.getLocalAddress() + ": 连接成功......").getBytes());
                        buffer.flip();
                        client.write(buffer);

                        new Thread(() -> {
                            while (true) {
                                try {
                                    buffer.clear();
                                    Scanner scanner = new Scanner(System.in);
                                    buffer.put(scanner.nextLine().getBytes());
                                    buffer.flip();
                                    client.write(buffer);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel readChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int count = readChannel.read(buffer);
                        if (count > 0) {
                            System.out.println(new String(buffer.array()));
                            buffer.clear();
                        }
                    }
                }

                selectionKeySet.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
