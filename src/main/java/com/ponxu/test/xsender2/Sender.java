package com.ponxu.test.xsender2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author xuwenzhao
 * @date 2016-09-01
 */
public class Sender {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("127.0.0.1", 9999));

        Selector selector= Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }
}