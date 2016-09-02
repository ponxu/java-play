package com.ponxu.test.xsender2;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-09-02
 */
public class IOLoop implements Runnable {
    private boolean isRunning;
    private Selector selector;

    public IOLoop() throws IOException {
        selector = Selector.open();
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if (!key.isValid()) {
                    continue;
                }

                OpHandler handler = (OpHandler) key.attachment();
                if (handler == null) {
                    continue;
                }

                try {
                    if (key.isAcceptable()) {
                        handler.doAccept(key);
                    } else if (key.isConnectable()) {
                        handler.doConnect(key);
                    } else if (key.isReadable()) {
                        handler.doRead(key);
                    } else if (key.isWritable()) {
                        handler.doWrite(key);
                    }
                } catch (Exception e) {
                    handler.occurError(key, e);
                }
            }
            keys.clear();
        }
        System.out.println("IOLoop stoped");
    }

    public void register(SelectableChannel channel, int ops, OpHandler handler) {
        try {
            channel.register(selector, ops, handler);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (isRunning) {
            isRunning = false;
            selector.wakeup();
        }
    }


    public static abstract class OpHandler {
        public void occurError(SelectionKey key, Exception e) {
            System.out.println(key);
            e.printStackTrace();
        }

        public void doAccept(SelectionKey key) throws IOException {
        }

        public void doConnect(SelectionKey key) throws IOException {
        }

        public void doRead(SelectionKey key) throws IOException {
        }

        public void doWrite(SelectionKey key) throws IOException {
        }
    }
}
