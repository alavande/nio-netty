package com.alavan.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Alavande
 */
public class NioTest2 {

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("nio-test2.txt");
        FileChannel fc = fis.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 将 fileChannel 中的数据写入 buffer
        fc.read(buffer);

        buffer.flip();

        // 读取直到 buffer 中不存在数据
        while (buffer.remaining() > 0) {
            System.out.println("Character: " + (char) buffer.get());
        }

        // FileInputStream 需要关闭
        // 而 FileChannel 的顶级父接口 Channel 继承了 Closeable 接口会自动关闭
        fis.close();
    }
}
