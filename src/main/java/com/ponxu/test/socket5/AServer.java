package com.ponxu.test.socket5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ponxu
 * @date 2016-11-05
 */
public class AServer implements Life {
    //private static final Logger LOG = Logger.getLogger(AServer.class.getSimpleName());
    private ServerSocketChannel ssc;
    private int port;
    private IOLoop downLoop;

    public AServer(int port) {
        this.port = port;
        this.downLoop = new IOLoop();
    }

    @Override
    public void startup() {
        try {
            initServerSocketChannel();
        } catch (IOException e) {
            Utils.throwAsRuntime(e);
        }

        downLoop.startup();
        System.out.println("lisent on: " + port);
    }

    private void initServerSocketChannel() throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().setReuseAddress(true);
        ssc.socket().bind(new InetSocketAddress("0.0.0.0", port));

        downLoop.register(ssc, SelectionKey.OP_ACCEPT, new IOLoop.AbsHandler() {
            @Override
            public void doAccept(SelectionKey key) throws IOException {
                SocketChannel client = ssc.accept();
                client.configureBlocking(false);
                System.out.println(client + " connected..");
            }
        });
    }

    @Override
    public void shutdown() {
        downLoop.shutdown();
    }

    public static void main(String[] args) {
        AServer as = new AServer(1080);
        as.startup();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                as.shutdown();
            }
        });
    }
}
