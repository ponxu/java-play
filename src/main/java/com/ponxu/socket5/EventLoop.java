package com.ponxu.socket5;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author xuwenzhao
 * @date 2016-11-04
 */
public class EventLoop implements Runnable, LifeCycle {
    private boolean isRunning;
    private Selector selector;
    private Handler handler;
    private List<Object[]> willEvents;

    public EventLoop(Selector selector, Handler handler) {
        if (selector == null || handler == null) {
            throw new IllegalArgumentException("parameter selector and handler is required");
        }
        this.selector = selector;
        this.handler = handler;
        this.isRunning = false;
        this.willEvents = new ArrayList<>();
    }

    public void register(SelectableChannel channel, int ops) {
        synchronized (willEvents) {
            willEvents.add(new Object[]{channel, ops});
            selector.wakeup();
        }
    }

    @Override
    public synchronized void startup() {
        if (!isRunning) {
            isRunning = true;
            new Thread(this).start();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (isRunning) {
            isRunning = false;
            selector.wakeup();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                doRegisterEvent();

                int n = selector.select();
                if (n <= 0) continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    try {
                        if (!key.isValid()) continue;
                        if (key.isAcceptable()) handler.doAccept(key);
                        else if (key.isReadable()) handler.doRead(key);
                        else if (key.isWritable()) handler.doWrite(key);
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
            }
        }
    }

    public static interface Handler {
        void doAccept(SelectionKey key) throws IOException;

        void doRead(SelectionKey key) throws IOException;

        void doWrite(SelectionKey key) throws IOException;
    }
}
