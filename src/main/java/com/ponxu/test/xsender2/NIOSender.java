package com.ponxu.test.xsender2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-09-01
 */
public class NIOSender {
    static String sendFile = "/home/xwz/下载/zookeeper-3.4.8.tar.gz";
    static String server = "127.0.0.1";
    static int port = 19999;

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.socket().setKeepAlive(true);
        sc.connect(new InetSocketAddress(server, port));

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        RandomAccessFile f = new RandomAccessFile(sendFile, "rw");
        FileChannel fc = f.getChannel();
        long offset = 0;
        long size = f.length();

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (!key.isValid()) {
                    continue;
                }

                SocketChannel channel = (SocketChannel) key.channel();

                if (key.isConnectable()) {
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                        key.cancel();
                    }
                    System.out.println("connected " + channel.socket().isClosed() + " " + channel.socket().isConnected());
                } else if (key.isReadable()) {
                    System.out.println("isReadable");
                    int len = channel.read(buffer);
                    System.out.println(len);

                    if (len == -1) {
                        channel.close();
                    }
                } else if (key.isWritable()) {
                    long tranSize = fc.transferTo(offset, 10240, channel);
                    System.out.println(tranSize);
                    offset += tranSize;
                    if (offset == size) {
                        channel.shutdownInput();
                        channel.shutdownOutput();
                        channel.close();
                        System.out.println("over");
                    }
                }
            }
            keys.clear();
        }
    }
}