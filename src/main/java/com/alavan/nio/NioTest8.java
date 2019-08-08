package com.alavan.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用 allocateNative() 方法
 * 直接将 buffer 分配到堆外内存中
 * @author Alavande
 */
public class NioTest8 {

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("input2.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("output2.txt");

        FileChannel fic = fileInputStream.getChannel();
        FileChannel foc = fileOutputStream.getChannel();

        // allocateDirect() 方法返回一个 DirectByteBuffer 对象
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        System.out.println(buffer.getClass());

        while(fic.read(buffer) > 0) {
            buffer.flip();
            foc.write(buffer);
            buffer.clear();
//            buffer.flip();
        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
