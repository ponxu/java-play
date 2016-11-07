package com.ponxu.test.socket5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-11-03
 */
public class TestNIO {
    public static void main(String[] args) {
        ProxyServer server = new ProxyServer(1080);
        server.init();
        server.startup();
    }
}

class ProxyServer implements LifeCycle {
    private int port;
    private ServerSocketChannel ssc;
    private EventLoop acceptLoop;
    private EventLoop clientLoop;

    public ProxyServer(int port) {
        this.port = port;
    }

    @Override
    public void init() {
        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().setReuseAddress(true);
            ssc.socket().bind(new InetSocketAddress("0.0.0.0", port));

            acceptLoop = new EventLoop();
            acceptLoop.init();

            clientLoop = new EventLoop();
            clientLoop.init();
        } catch (IOException e) {
            RuntimeException re = new IllegalStateException();
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    @Override
    public void startup() {
        acceptLoop.startup();
        clientLoop.startup();

        try {
            acceptLoop.register(ssc, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            RuntimeException re = new IllegalStateException();
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    @Override
    public void shutdown() {
        acceptLoop.shutdown();
        clientLoop.shutdown();
    }
}

class EventLoop extends Thread implements LifeCycle {
    private Selector selector;
    private boolean isRunning;

    @Override
    public void init() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            RuntimeException re = new IllegalStateException();
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    @Override
    public synchronized void startup() {
        if (!isRunning) {
            isRunning = true;
            super.start();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (isRunning) {
            isRunning = false;
            selector.wakeup();
        }
    }

    public void register(SelectableChannel channel, int ops) throws ClosedChannelException {
        channel.register(selector, ops);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                int n = selector.select();
                if (n <= 0) continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    try {
                        if (!key.isValid()) continue;
                        if (key.isAcceptable()) doAccept(key);
                        else if (key.isReadable()) doRead(key);
                        else if (key.isWritable()) doWrite(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                keys.clear();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel channel = ssc.accept();
    }

    private void doRead(SelectionKey key) {
    }

    private void doWrite(SelectionKey key) {
    }
}

abstract class EventHandler {
    public void onAccept() {
    }

    public void onRead() {
    }

    public void onWrite() {
    }
}

interface LifeCycle {
    void init();

    void startup();

    void shutdown();
}