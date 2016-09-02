package com.ponxu.test.xsender2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuwenzhao
 * @date 2016-09-01
 */
public class NIOSender2 extends IOLoop.OpHandler implements Runnable {
    private static int CHUNK_SIZE = 1024;

    private String file;

    private IOLoop loop;
    private SocketChannel channel;
    private ByteBuffer buffer;
    private Condition write;

    public NIOSender2(IOLoop loop, String file, String server, int port) throws IOException {
        this.loop = loop;
        this.file = file;

        this.buffer = ByteBuffer.allocate(1024);
        this.write = new ReentrantLock().newCondition();

        // open connection
        this.channel = SocketChannel.open();
        this.channel.configureBlocking(false);
        this.channel.socket().setKeepAlive(true);
        this.channel.connect(new InetSocketAddress(server, port));
        loop.register(channel, SelectionKey.OP_CONNECT, this);
    }

    @Override
    public void run() {
        try {
            RandomAccessFile f = new RandomAccessFile(file, "rw");
            FileChannel fc = f.getChannel();
            long offset = 0;
            long size = f.length();

            write.await();

            while (offset < size) {
                long tranSize = fc.transferTo(offset, CHUNK_SIZE, channel);
                System.out.println(tranSize);
                offset += tranSize;

                if (tranSize < CHUNK_SIZE && offset < size) {
                    loop.register(channel, SelectionKey.OP_WRITE, this);
                    write.await();
                    loop.register(channel, 0, this);
                }
            }

            channel.shutdownInput();
            channel.shutdownOutput();
            channel.close();
            System.out.println("over");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doConnect(SelectionKey key) throws IOException {
        if (channel.isConnectionPending()) {
            channel.finishConnect();
            write.signal();
        }
        System.out.println("connected " + channel.socket().isClosed() + " " + channel.socket().isConnected());
    }

    @Override
    public void doRead(SelectionKey key) throws IOException {
        System.out.println("isReadable");
        int len = channel.read(buffer);
        System.out.println(len);

        if (len == -1) {
            channel.close();
        }
    }

    @Override
    public void doWrite(SelectionKey key) throws IOException {
        write.signal();
    }

    public static void main(String[] args) throws IOException {
        IOLoop loop = new IOLoop();
        new Thread(loop).start();

        String sendFile = "/home/xwz/下载/zookeeper-3.4.8.tar.gz";
        String server = "127.0.0.1";
        int port = 19999;

        NIOSender2 sender = new NIOSender2(loop, sendFile, server, port);
        new Thread(sender).start();
    }
}