package com.ponxu.onepiece.nio;

import com.ponxu.onepiece.Handler;
import com.ponxu.onepiece.Server;
import com.ponxu.onepiece.codec.Decoder;
import com.ponxu.onepiece.codec.Encoder;
import com.ponxu.onepiece.codec.impl.NoopDecoder;
import com.ponxu.onepiece.codec.impl.NoopEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOServer implements Server {
    // configs
    private String hostName = "0.0.0.0";
    private int port = 0;
    private int workerCount = 1;
    protected Handler handler;
    protected Decoder decoder = new NoopDecoder();
    protected Encoder encoder = new NoopEncoder();

    // internals
    private ServerSocketChannel serverChannel;
    private NIOEventLoop bossEventLoop;
    private NIOEventLoop[] workEventLoops;


    // TODO builder
    public NIOServer() {
    }

    public NIOServer build() {
        if (hostName == null)
            throw new IllegalArgumentException("address cann't be null");
        if (port <= 0)
            throw new IllegalArgumentException("port cann't be less than 0: " + port);
        if (workerCount < 0)
            throw new IllegalArgumentException("worker count cann't be less than 0: " + workerCount);

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().setReuseAddress(true);
            serverChannel.socket().bind(new InetSocketAddress(hostName, port));

            workEventLoops = new NIOEventLoop[workerCount];
            for (int i = 0; i < workerCount; i++) {
                workEventLoops[i] = new NIOEventLoop(this);
            }
            bossEventLoop = new NIOEventLoop(this, workEventLoops);
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    // ================ builder =========================
    public NIOServer hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public NIOServer port(int port) {
        this.port = port;
        return this;
    }

    public NIOServer workerCount(int workerCount) {
        this.workerCount = workerCount;
        return this;
    }

    public NIOServer handler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public NIOServer decoder(Decoder decoder) {
        this.decoder = decoder;
        return this;
    }

    public NIOServer encoder(Encoder encoder) {
        this.encoder = encoder;
        return this;
    }
}
