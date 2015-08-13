package com.ponxu.test.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOServer implements Server {
    private ServerSocketChannel serverChannel;
    private NIOEventLoop bossEventLoop;
    private NIOEventLoop[] workEventLoops;
    private AtomicLong connectCounter;

    public NIOServer(int port) {
        this("0.0.0.0", port, 1);
    }

    public NIOServer(String address, int port, int workers) {
        if (address == null)
            throw new IllegalArgumentException("address cann't be null");
        if (workers < 0)
            throw new IllegalArgumentException("workers cann't be less than 0: " + port);

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().setReuseAddress(true);
            serverChannel.socket().bind(new InetSocketAddress(address, port));

            workEventLoops = new NIOEventLoop[workers];
            for (int i = 0; i < workEventLoops.length; i++) {
                workEventLoops[i] = new NIOEventLoop();
            }
            bossEventLoop = new NIOEventLoop(workEventLoops);

            connectCounter = new AtomicLong(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws IOException {
        bossEventLoop.registerEvent(serverChannel, SelectionKey.OP_ACCEPT);

        bossEventLoop.start();
        for (NIOEventLoop worker : workEventLoops)
            worker.start();
    }

    @Override
    public void stop() throws IOException {
        bossEventLoop.close();
        for (NIOEventLoop worker : workEventLoops)
            worker.close();
    }
}
