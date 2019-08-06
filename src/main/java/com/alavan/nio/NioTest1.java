package com.alavan.nio;

import java.nio.IntBuffer;
import java.security.SecureRandom;

/**
 * @author Alavande
 */
public class NioTest1 {

    public static void main(String[] args) {
        // 抽象类, 需要通过 allocate() 或者 allocateDirect() 方法创建
        // 返回的其实是 HeapIntBuffer
        // buffer 底层用数组存储数据
        IntBuffer buffer = IntBuffer.allocate(10);
        System.out.println(String.format("初始化时 capacity: %s, limit: %s, position: %s",
                buffer.capacity(), buffer.limit(), buffer.position()));

        for (int i = 0; i < 10; i++) {
            // SecureRandom 比 Random 随机性更好
            int randomInt = new SecureRandom().nextInt(20);
            buffer.put(randomInt);
        }

        System.out.println(String.format("flip 前 limit: %s, position: %s",
                buffer.limit(), buffer.position()));

        /**
         * 写完后翻转 buffer 以开始读取
         * flip() 方法:
         * <code>
         *     limit = position;
         *     position = 0;
         *     mark = -1;
         * </code>
         */
        buffer.flip();

        System.out.println(String.format("flip 后 limit: %s, position: %s",
                buffer.limit(), buffer.position()));

        while (buffer.hasRemaining()) {
            System.out.println(String.format("while 循环时 capacity: %s, limit: %s, position: %s",
                    buffer.capacity(), buffer.limit(), buffer.position()));
            System.out.println("取出随机值: " + buffer.get());
        }
    }
}
