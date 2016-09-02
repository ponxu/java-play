package com.ponxu.test.xsender2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-09-02
 */
public class NIOReceiver {
    static String tempFile = "/tmp/test.xsender";
    static int port = 19999;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        RandomAccessFile af = new RandomAccessFile(tempFile, "rw");
        FileChannel fc = af.getChannel();
        long offset = 0;

        // long size = new RandomAccessFile("/home/xwz/下载/zookeeper-3.4.8.tar.gz", "rw").length();

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.socket().setKeepAlive(true);
                    sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    sc.write(ByteBuffer.wrap(new byte[]{0, 0}));
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    long len = fc.transferFrom(channel, offset, 1024);
                    offset += len;
                    System.out.println(len);

                    // test server close
                    channel.shutdownInput();
                    channel.shutdownOutput();
                    channel.close();
                    System.out.println("close client");
                }
            }
            keys.clear();
        }
    }
}
