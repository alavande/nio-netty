package com.alavan.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Alavande
 */
public class NioTest4 {

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("input.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("output.txt");

        FileChannel fic = fileInputStream.getChannel();
        FileChannel foc = fileOutputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(4);

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
