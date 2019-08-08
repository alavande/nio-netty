package com.alavan.nio;

import java.nio.ByteBuffer;

/**
 * @author Alavande
 */
public class NioTest5 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.putInt(15);
        buffer.putLong(10000000000L);
        buffer.putDouble(1.24259);
        buffer.putChar('我');
        buffer.putFloat(1.25f);
        buffer.putShort((short) 2);

        buffer.flip();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getDouble());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getFloat());
        System.out.println(buffer.getShort());

    }
}
