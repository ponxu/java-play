package com.ponxu.socket5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

/**
 * @author xuwenzhao
 * @date 2016-11-04
 */
public class AServer implements LifeCycle {
    private static final int BUF_SIZE = 4 * 1024;

    private ServerSocketChannel ssc;
    private EventLoop downLoop;

    public AServer(int port) throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().setReuseAddress(true);
        ssc.socket().bind(new InetSocketAddress("0.0.0.0", port));

        downLoop = new EventLoop(Selector.open(), downHandler);
    }

    @Override
    public void startup() {
        downLoop.startup();
        downLoop.register(ssc, OP_ACCEPT);

        System.out.println("startup....");
    }

    @Override
    public void shutdown() {
        downLoop.shutdown();
    }

    private EventLoop.Handler downHandler = new EventLoop.Handler() {
        @Override
        public void doAccept(SelectionKey key) throws IOException {
            SocketChannel channel = ssc.accept();
            System.out.println("accept: " + channel);

            channel.configureBlocking(false);
            channel.socket().setTcpNoDelay(true);
            downLoop.register(channel, OP_READ);
        }

        @Override
        public void doRead(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("read....");

            ByteBuffer buffer = (ByteBuffer) key.attachment();
            if (buffer == null) {
                buffer = ByteBuffer.allocateDirect(BUF_SIZE);
            }

            channel.read(buffer);
        }

        @Override
        public void doWrite(SelectionKey key) throws IOException {
        }
    };

    public static void main(String[] args) throws IOException {
        new AServer(1080).startup();
    }
}


