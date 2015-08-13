package com.ponxu.test;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xwz on 15-8-13.
 */
public class TestNIOSocket {
    public static void main(String[] args) throws Exception {
        NIOEventLoop workerEventLoop = new NIOEventLoop(new NIOEventHandler() {
            @Override
            public void onRead(SocketChannel channel, ByteBuffer buffer) throws IOException {
                channel.write(buffer);
            }
        });

        NIOEventLoop bossEventLoop = new NIOEventLoop(new NIOEventHandler() {
            @Override
            public void onAccept(ServerSocketChannel server, SocketChannel client) throws IOException {
                client.configureBlocking(false);
                workerEventLoop.registerEvent(client, SelectionKey.OP_READ);
            }
        });

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.socket().setReuseAddress(true);
        server.socket().bind(new InetSocketAddress(8080));

        bossEventLoop.registerEvent(server, SelectionKey.OP_ACCEPT);

        bossEventLoop.start();
        workerEventLoop.start();

        bossEventLoop.join();
        workerEventLoop.join();
    }
}

class NIOEventLoop extends Thread implements Closeable {
    private static final AtomicInteger IDX = new AtomicInteger(0);
    private Selector selector;
    private boolean isLoop;
    private ByteBuffer buffer;
    private NIOEventHandler handler;

    public NIOEventLoop(NIOEventHandler handler) throws IOException {
        super("NIOEventLoop-" + IDX.incrementAndGet());
        selector = Selector.open();
        isLoop = true;
        buffer = ByteBuffer.allocate(1024);
        this.handler = handler;
    }

    public SelectionKey registerEvent(SelectableChannel channel, int ops) throws ClosedChannelException {
        return channel.register(selector, ops);
    }

    @Override
    public void run() {
        while (isLoop) {
            onceLoop();
        }
    }

    private void onceLoop() {
        try {
            int i = selector.select(10000);
            System.out.println("select: " + i);

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey readyKey = it.next();
                it.remove();

                if (readyKey.isAcceptable()) doAccept(readyKey);
                if (readyKey.isReadable()) doRead(readyKey);
                if (readyKey.isWritable()) doRead(readyKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAccept(SelectionKey readyKey) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) readyKey.channel();
        SocketChannel client = server.accept();
        System.out.println("Accept: " + client);

        if (handler != null) handler.onAccept(server, client);
    }

    private void doRead(SelectionKey readyKey) throws IOException {
        SocketChannel channel = (SocketChannel) readyKey.channel();

        buffer.clear();
        int n = channel.read(buffer);
        System.out.println("Read: " + n);

        if (n == -1) {
            channel.close();
            if (handler != null) handler.onClose(channel);
        } else {
            buffer.flip();
            if (handler != null) handler.onRead(channel, buffer);
        }
    }

    private void doWrite(SelectionKey readyKey) throws IOException {

    }

    @Override
    public void close() throws IOException {
        isLoop = true;
    }
}

abstract class NIOEventHandler {
    public void onAccept(ServerSocketChannel server, SocketChannel client) throws IOException {
    }

    public void onRead(SocketChannel channel, ByteBuffer buffer) throws IOException {
    }

    public void onClose(SocketChannel channel) throws IOException {
    }
}