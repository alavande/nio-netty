package com.alavan.nio;

import java.nio.ByteBuffer;

/**
 * Buffer 切割
 * 可以通过设定 position 和 limit 后
 * 通过 slice() 方法切割一个子 buffer
 * 两个 buffer 中的共同数据通用
 * @author Alavande
 */
public class NioTest6 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        for (int i = 0; i < 10; i++) {
            buffer.put((byte) i);
        }

        // 通过设定 position 和 limit , 再调用 slice() 方法
        // 切割出一个子 ByteBuffer
        // 两个 buffer 中共同数据是通用的, 修改一个的值, 另一个 buffer 中的也会变
        buffer.position(2);
        buffer.limit(6);

        ByteBuffer buffer1 = buffer.slice();

        for (int i = 0; i < buffer1.capacity(); i++) {
            // 两个 buffer 数据是通用的
            byte b = buffer1.get(i);
            b *= 2;
            buffer1.put(i, b);
        }

        buffer.clear();
        System.out.println(String.format("capacity: %s, position: %s, limit: %s",
                buffer.capacity(), buffer.position(), buffer.limit()));

//        buffer.position(0);
//        buffer.limit(buffer.capacity());

        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
    }
}
