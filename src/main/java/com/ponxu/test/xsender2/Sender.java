package com.ponxu.test.xsender2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-09-01
 */
public class Sender {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("127.0.0.1", 9999));

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    System.out.println("connect finished");
                } else if (key.isReadable()) {
                    // System.out.println("isReadable");
                } else if (key.isWritable()) {

                    //System.out.println("isWritable");
                }
            }
            keys.clear();
        }
    }
}