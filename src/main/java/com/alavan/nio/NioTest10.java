package com.alavan.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * NIO 文件锁
 * @author Alavande
 */
public class NioTest10 {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("nio-test10.txt", "rw");
        FileChannel fileChannel = file.getChannel();

        // 参数: 锁定起始位置, 锁定大小, 是否共享锁
        FileLock fileLock = fileChannel.lock(0, 3, true);
        // isValid() 锁是否有效, isShared() 是否共享锁
        System.out.println(String.format("valid: %s, shared: %s", fileLock.isValid(), fileLock.isShared()));
        fileLock.release();
        //  实现了 Closeable 接口, 不需要手动关闭
//        file.close();
    }
}
