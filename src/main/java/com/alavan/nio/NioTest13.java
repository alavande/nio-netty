package com.alavan.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * 字符集解析
 * @author Alavande
 */
public class NioTest13 {

    public static void main(String[] args) throws IOException {
        String inputFile = "nio-test13-in.txt";
        String outputFile = "nio-test13-out.txt";

        RandomAccessFile inFile = new RandomAccessFile(inputFile, "r");
        RandomAccessFile outFile = new RandomAccessFile(outputFile, "rw");

        long inLength = new File(inputFile).length();

        FileChannel inChannel = inFile.getChannel();
        FileChannel outChannel = outFile.getChannel();

        MappedByteBuffer inData = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inLength);

        System.out.println("===================================");
        // 查看当前系统所有可用的字符集
        Charset.availableCharsets().forEach((k, v) -> {
            System.out.println(k + ", " + v);
        });
        System.out.println("===================================");

        // iso-8859-1 不会丢失字节中任何一个位的信息
        //
        //
        //
        // unicode 固定采用两个字节来表示一个字符, 集合了全世界大部分字符
        // unicode 是一种编码方式, 而 UTF (Unicode Translation Format)是一种存储方式
        // UTF-16 有 UTF-16BE(big endian), UTF-16LE(little endian) 两种
        // 两种区别在于每行前面的一个隐藏字符(Zero Width No-Break Space)不同
        // BE (0xFEFF), LE (0xFFFE)
        // UTF-8 是 unicode 的实现方式之一, 是变长字节表示形式
        // UTF-8 中, 不同的字符表现的长度不一样
        // 如: 英文数字等使用一个字节表示, 且与 ascii 及 iso-8859-1 是一样的
        // 而中文一般则是使用三个字节表示
        // 最多可以用六个字节表示一个字符
        // UTF-8 中有一个 BOM (Byte Order Mark, 字节序) 的概念
        // 如果文件起始位置有 (Zero Width No-Break Space) 则是带 BOM, 没有则是不带 BOM 的
        // BOM 一般是 windows 系统插入的, Linux 和 MAC 上的 UTF-8 文件是不带这个头的
        // BOM 可能引起某些不做特殊处理的软件的解析问题
        Charset charset = Charset.forName("iso-8859-1");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        // 对磁盘文件读取的字节流, 采用定义的字符集解码成字符流
        CharBuffer charBuffer = decoder.decode(inData);
        ByteBuffer outData = encoder.encode(charBuffer);

        outChannel.write(outData);

        inFile.close();
        outFile.close();
    }
}
