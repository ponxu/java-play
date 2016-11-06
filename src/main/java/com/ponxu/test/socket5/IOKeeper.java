package com.ponxu.test.socket5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ponxu
 * @date 2016-11-06
 */
public class IOKeeper {
    private SocketChannel channel;
    private boolean isReadOver;
    private boolean isWriteOver;
    private boolean isFirstWrite;

    public IOKeeper(SocketChannel channel) {
        this.channel = channel;
        this.isReadOver = true;
        this.isWriteOver = true;
        this.isFirstWrite = true;
    }

    public boolean read(ByteBuffer buffer, int len, IOPromise promise) throws IOException {
        if (isReadOver) {
            isReadOver = false;
            buffer.clear();
            buffer.limit(len);
        }

        channel.read(buffer);
        isReadOver = !buffer.hasRemaining();

        if (isReadOver) {
            invokePromise(promise);
        }

        return isReadOver;
    }

    public boolean write(ByteBuffer buffer, IOPromise promise) throws IOException {
        if (isWriteOver) {
            isWriteOver = false;
            isFirstWrite = true;
            buffer.flip();
        }

        channel.write(buffer);
        isWriteOver = !buffer.hasRemaining();

        if (isWriteOver) {
            if (!isFirstWrite) {
                // unregister write
            }
        } else {
            if (isFirstWrite) {
                // register write
            }
        }
        isFirstWrite = false;

        if (isWriteOver) {
            invokePromise(promise);
        }

        return isWriteOver;
    }

    private void invokePromise(IOPromise promise) {
        try {
            if (promise != null) {
                promise.done();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
