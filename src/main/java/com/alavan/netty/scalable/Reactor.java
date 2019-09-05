package com.alavan.netty.scalable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

class Reactor implements Runnable {

    final Selector selector;
    final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException {
        //Reactor初始化
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        //非阻塞
        serverSocket.configureBlocking(false);
        //分步处理,第一步,接收accept事件
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        //attach callback object, Acceptor
        sk.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 阻塞等待连接
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    // Reactor 负责 dispatch 收到的事件
                    dispatch((SelectionKey) (it.next()));
                }
                selected.clear();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void dispatch(SelectionKey k) {
        // 调用之前注册的 callback 对象 (Acceptor)
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        }
    }

    class Acceptor implements Runnable {
        // inner
        @Override
        public void run() {
            try {
                // 使用 Reactor 中的 serverSocket 获取 SocketChannel 连接
                SocketChannel c = serverSocket.accept();
                if (c != null) {
                    // 在 Handler 中处理具体逻辑
                    new Handler(selector, c);
                }
            }
            catch(IOException ex) { /* ... */ }
        }
    }
}

final class Handler implements Runnable {
    final SocketChannel socket;
    final SelectionKey sk;

    final int MAXIN = 4096;
    final int MAXOUT = 4096;

    ByteBuffer input = ByteBuffer.allocate(MAXIN);
    ByteBuffer output = ByteBuffer.allocate(MAXOUT);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socket.register(sel, 0);
        //将Handler作为callback对象
        sk.attach(this);
        //第二步,接收Read事件
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }

    boolean inputIsComplete() {
        /* ... */
        return true;
    }
    boolean outputIsComplete() {
        /* ... */
        return true;
    }
    void process() { /* ... */ }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            }
            else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            state = SENDING;
            // Normally also do first write now
            //第三步,接收write事件
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }
    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete()) {
            //write完就结束了, 关闭select key
            sk.cancel();
        }
    }
}

//上面 的实现用Handler来同时处理Read和Write事件, 所以里面出现状态判断
//我们可以用State-Object pattern来更优雅的实现
//class Handler {
//
//    // ...
//
//    public void run() {
//        // initial state is reader
//        socket.read(input);
//        if (inputIsComplete()) {
//            process();
//            //状态迁移, Read后变成write, 用Sender作为新的callback对象
//            sk.attach(new Sender());
//            sk.interest(SelectionKey.OP_WRITE);
//            sk.selector().wakeup();
//        }
//    }
//    class Sender implements Runnable {
//
//        @Override
//        public void run(){
//            // ...
//            socket.write(output);
//            if (outputIsComplete()) {
//                sk.cancel();
//            }
//        }
//    }
//}
