package com.alavan.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 内存映射文件 MappedByteBuffer
 * DirectByteBuffer 继承了 MappedByteBuffer
 * 可以通过 {@link FileChannel#map} 创建一个 MappedByteBuffer
 * MappedByteBuffer 位于堆外内存, 可以将文件的全部或者部分读取到内存
 * 程序只跟内存数据交互, 操作系统会将内存数据变化写入磁盘
 * @author Alavande
 */
public class NioTest9 {

    public static void main(String[] args) throws IOException {
        // 参数: 文件名, 读写状态
        RandomAccessFile file = new RandomAccessFile("nio-test9.txt", "rw");
        FileChannel fileChannel = file.getChannel();
        // 参数: 模式, 起始位置, 映射大小
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0, (byte) 'a');
        mappedByteBuffer.put(3, (byte) 'b');

        //  实现了 Closeable 接口, 不需要手动关闭
//        file.close();
    }
}
