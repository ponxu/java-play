package com.ponxu.onepiece.nio;

import com.ponxu.onepiece.Connection;
import com.ponxu.onepiece.Handler;

/**
 * Created by xwz on 15/8/16.
 */
public class NIOHandler implements Handler {
    private NIOServer server;

    public NIOHandler(NIOServer server) {
        this.server = server;
    }

    @Override
    public void onConnected(Connection conn) {
        System.out.println("onConnected: " + conn);
        server.handler.onConnected(conn);
    }

    @Override
    public void onRead(Connection conn, Object receivedData) {
        System.out.println("onRead: " + conn);
        server.handler.onRead(conn, receivedData);
    }

    @Override
    public void onClosed(Connection conn) {
        System.out.println("onClosed: " + conn);
    }

    @Override
    public void onError(Connection conn, Throwable cause) {
        System.out.println("onError: " + conn);
    }
}
