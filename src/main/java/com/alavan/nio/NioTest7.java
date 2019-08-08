package com.alavan.nio;

import java.nio.ByteBuffer;

/**
 * 可以通过调用 asReadOnlyBuffer() 方法
 * 返回一个只读 buffer, 此 buffer 无法转换为可写入的 buffer
 * @author Alavande
 */
public class NioTest7 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        System.out.println(buffer.getClass());
        for (int i = 0; i < 10; i++) {
            buffer.put((byte) i);
        }

        ByteBuffer readBuffer = buffer.asReadOnlyBuffer();
        System.out.println(readBuffer.getClass());

        for (int i = 0; i < readBuffer.capacity(); i++) {
            System.out.println(readBuffer.get(i));
        }
    }
}
