package com.ponxu.test.xsender2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
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
        sc.connect(new InetSocketAddress("127.0.0.1", 19999));

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);

        RandomAccessFile f = new RandomAccessFile("/home/xwz/下载/zookeeper-3.4.8.tar.gz", "rw");
        FileChannel fc = f.getChannel();
        long offset = 0;
        long size = f.length();

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                SocketChannel channel = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }
                    System.out.println("connected " + channel.socket());
                } else if (key.isReadable()) {
                    System.out.println("isReadable");

                } else if (key.isWritable()) {
                    long tranSize = fc.transferTo(offset, 10240, channel);
                    offset += tranSize;

                    if (offset == size) {
                        channel.close();
                        System.out.println("over");
                    }
                }
            }
            keys.clear();
        }
    }
}