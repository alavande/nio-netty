package com.alavan.zerocopy.newio;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Alavande
 */
public class NewIOClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        // 配置阻塞形式, 否则文件可能无法发送完全
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress("localhost", 8899));

        String filePath = "F:\\书籍\\亿级流量网站架构核心技术.pdf";

        FileChannel fileChannel = new FileInputStream(filePath).getChannel();

        long startTime = System.currentTimeMillis();

        long count = 0;
        while (count < fileChannel.size()) {

            // transferTo() 方法将 fileChannel 关联的文件的字节传递到可写的 channel (socketChannel) 中
            // 此方法效率高于简单循环读取写入
            // 许多操作系统可以直接从 filesystem cache 中直接传递字节到目标 channel 中, 而不需要先拷贝它们
            // 即采用了操作系统的零拷贝
            //
            // 在 windows 中, 如果文件大小大于 8mb, 则要进行多次传送
            // 大文件会被拆分为 8mb 大小的分段文件, 且此时要注意文件位置
            count += fileChannel.transferTo(count, fileChannel.size(), socketChannel);
        }


        System.out.println("发送总字节数: " + count + ", 耗时: " + (System.currentTimeMillis() - startTime));

        fileChannel.close();
    }
}
