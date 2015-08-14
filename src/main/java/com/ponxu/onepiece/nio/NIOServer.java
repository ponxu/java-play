package com.ponxu.onepiece.nio;

import com.ponxu.onepiece.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOServer implements Server {
    private String hostName;
    private int port;
    private int workerCount;

    private ServerSocketChannel serverChannel;
    private NIOEventLoop bossEventLoop;
    private NIOEventLoop[] workEventLoops;

    public NIOServer(int port) {
        this("0.0.0.0", port, 1);
    }

    public NIOServer(String hostName, int port, int workerCount) {
        if (hostName == null)
            throw new IllegalArgumentException("address cann't be null");
        if (workerCount < 0)
            throw new IllegalArgumentException("workers cann't be less than 0: " + port);

        this.hostName = hostName;
        this.port = port;
        this.workerCount = workerCount;

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().setReuseAddress(true);
            serverChannel.socket().bind(new InetSocketAddress(hostName, port));

            workEventLoops = new NIOEventLoop[workerCount];
            for (int i = 0; i < workerCount; i++) {
                workEventLoops[i] = new NIOEventLoop();
            }
            bossEventLoop = new NIOEventLoop(workEventLoops);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws IOException {
        bossEventLoop.registerEvent(serverChannel, SelectionKey.OP_ACCEPT);

        // start loop
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
