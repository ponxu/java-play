package com.ponxu.onepiece.nio;

import com.ponxu.onepiece.Connection;
import com.ponxu.onepiece.Handler;
import com.ponxu.onepiece.codec.Decoder;
import com.ponxu.onepiece.codec.Encoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOEventLoop extends Thread {
    private NIOServer server;
    private boolean isRunning;
    private Selector selector;
    private ByteBuffer buffer;

    private List<Object[]> willEvents;
    // accept deal
    private AtomicLong acceptCounter;
    private NIOEventLoop[] workEventLoops;

    private NIOHandler handler;


    public NIOEventLoop(NIOServer server) throws IOException {
        this(server, null);
    }

    public NIOEventLoop(NIOServer server, NIOEventLoop[] workEventLoops) throws IOException {
        super("NIOEventLoop-" + (workEventLoops == null ? "Worker" : "Boss"));

        this.server = server;
        this.isRunning = true;
        this.selector = Selector.open();
        this.buffer = ByteBuffer.allocateDirect(1024 * 64);

        this.willEvents = new ArrayList<>();

        this.acceptCounter = new AtomicLong(0);
        this.workEventLoops = workEventLoops;
        this.handler = new NIOHandler(server);
    }

    public void registerEvent(SelectableChannel channel, int ops) {
        synchronized (willEvents) {
            willEvents.add(new Object[]{channel, ops});
            System.out.println("Will register event: " + channel + " " + ops);
            selector.wakeup();
        }
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
                // register event
                doRegisterEvent();

                // wait event
                int n = selector.select();
                System.out.println("Select: " + n);
                if (n <= 0) continue;

                // deal with event TODO use iterator
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

    private void doRegisterEvent() throws ClosedChannelException {
        synchronized (willEvents) {
            Iterator<Object[]> it = willEvents.iterator();
            while (it.hasNext()) {
                Object[] event = it.next();
                it.remove();

                SelectableChannel channel = (SelectableChannel) event[0];
                int ops = (int) event[1];
                SelectionKey key = channel.register(selector, ops);
                System.out.println("Register event: " + channel + " " + ops);

                // if reg read: deal connected callback
                if ((ops & SelectionKey.OP_READ) != 0) {
                    Connection conn = new NIOConnection((SocketChannel) channel);
                    key.attach(conn);
                    handler.onConnected(conn);
                }
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
            doClose(key);
        } else {
            buffer.flip();
            // deal read callback
            Connection conn = (Connection) key.attachment();
            handler.onRead(conn, buffer);
        }
    }

    private void doClose(SelectionKey key) throws IOException {
        key.channel().close();
        System.out.println("Key size: " + selector.keys().size());

        // deal closed callback
        Connection conn = (Connection) key.attachment();
        handler.onClosed(conn);
    }

    private void doWrite(SelectionKey key) throws IOException {
    }
}