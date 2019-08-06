package com.alavan.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Alavande
 */
public class NioTest3 {

    public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("nio-test3.txt");
        FileChannel fc = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] msg = "hello world, welcome".getBytes();

        for (int i = 0; i < msg.length; i++) {
            buffer.put(msg[i]);
        }

        buffer.flip();

        // 将 buffer 中的数据写入 fileChannel 中, 实际上是写出到文件中
        fc.write(buffer);

        fos.close();
    }
}
