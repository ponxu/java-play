package com.ponxu.test.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOEventLoop extends Thread {
    private boolean isRunning;
    private Selector selector;
    private ByteBuffer buffer;

    private AtomicLong acceptCounter;
    private NIOEventLoop[] workEventLoops;

    public NIOEventLoop() throws IOException {
        this(null);
    }

    public NIOEventLoop(NIOEventLoop[] workEventLoops) throws IOException {
        super("NIOEventLoop-" + workEventLoops == null ? "Boss" : "Worker");

        isRunning = true;
        selector = Selector.open();
        buffer = ByteBuffer.allocate(1024);

        this.acceptCounter = new AtomicLong(0);
        this.workEventLoops = workEventLoops;
    }

    public SelectionKey registerEvent(SelectableChannel channel, int ops) throws ClosedChannelException {
        selector.wakeup();
        return channel.register(selector, ops);
    }

    public void close() throws IOException {
        isRunning = false;
        selector.wakeup();
        selector.close();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                int n = selector.select(10000);
                System.out.println("Select: " + n);
                if (n <= 0) continue;

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) doAccept(key);
                    else if (key.isReadable()) doRead(key);
                    else if (key.isWritable()) doWrite(key);
                }
                selectedKeys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        long idx = acceptCounter.getAndIncrement();
        NIOEventLoop worker = workEventLoops != null && workEventLoops.length > 0
                ? workEventLoops[(int) (idx % workEventLoops.length)]
                : this;
        worker.registerEvent(client, SelectionKey.OP_READ);
        System.out.println("Accept: " + client + "   idx: " + idx);
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        buffer.clear();
        int n = channel.read(buffer);
        System.out.println("Read: " + n);

        if (n == -1) {
            channel.close();
            System.out.println("Key size: " + selector.keys().size());
        } else {
            buffer.flip();
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
    }
}