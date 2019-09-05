package com.alavan.zerocopy.oldio;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 旧客户端
 * @author Alavande
 */
public class OldIOClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 8899);

        String filePath = "F:\\书籍\\亿级流量网站架构核心技术.pdf";

        InputStream inputStream = new FileInputStream(filePath);

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        byte[] bytes = new byte[4096];
        int count = 0;
        int total = 0;

        long startTime = System.currentTimeMillis();

        while ((count = inputStream.read(bytes, 0, bytes.length)) != -1) {
            total += count;
            dataOutputStream.write(bytes);
        }

        System.out.println("发送总字节数: " + total + ", 耗时: " + (System.currentTimeMillis() - startTime));
        dataOutputStream.close();
        inputStream.close();
        socket.close();
    }

}
