package com.ponxu.test.socket5;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 * @author ponxu
 * @date 2016-11-05
 */
public class IOLoop implements Runnable, Life {
    private Selector selector;
    private boolean isRunning;

    public IOLoop() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            Utils.throwAsRuntime(e);
        }
    }

    public void register(SelectableChannel channel, int ops, Handler op) throws ClosedChannelException {
        SelectionKey key = channel.register(selector, ops);
        key.attach(op);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                int n = selector.select();
                if (n <= 0) continue;
            } catch (Throwable t) {
                t.printStackTrace();
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                Handler op;
                if (!key.isValid() || (op = (Handler) key.attachment()) == null) {
                    continue;
                }

                try {
                    if (key.isAcceptable()) {
                        op.doAccept(key);
                    } else if (key.isReadable()) {
                        op.doRead(key);
                    } else if (key.isWritable()) {
                        op.doWrite(key);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            keys.clear();
        }
    }

    @Override
    public synchronized void startup() {
        if (!isRunning) {
            isRunning = true;
            new Thread(this).start();
            System.out.println(getClass().getSimpleName() + " startup..");
        }
    }

    @Override
    public synchronized void shutdown() {
        if (isRunning) {
            isRunning = false;
            selector.wakeup();
            System.out.println(getClass().getSimpleName() + " shutdown..");
        }
    }

    public static interface Handler {
        public void doAccept(SelectionKey key) throws IOException;

        public void doRead(SelectionKey key) throws IOException;

        public void doWrite(SelectionKey key) throws IOException;
    }

    public static class AbsHandler implements Handler {
        @Override
        public void doAccept(SelectionKey key) throws IOException {
        }

        @Override
        public void doRead(SelectionKey key) throws IOException {
        }

        @Override
        public void doWrite(SelectionKey key) throws IOException {
        }
    }
}
