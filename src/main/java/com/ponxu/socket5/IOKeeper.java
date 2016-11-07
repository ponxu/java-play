package com.ponxu.socket5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author xuwenzhao
 * @date 2016-11-04
 */
public class IOKeeper {
    private boolean isReadOver;
    private SocketChannel channel;
    private ByteBuffer buffer;

    public IOKeeper(SocketChannel channel, ByteBuffer buffer) {
        this.channel = channel;
        this.buffer = buffer;
        this.isReadOver = true;
    }

    public boolean read(int len) throws IOException {
        // TODO limit check
        if (isReadOver) {
            isReadOver = false;
            buffer.clear();
            buffer.limit(len);
        }

        channel.read(buffer);

        if (!buffer.hasRemaining()) {
            isReadOver = true;
        }

        return isReadOver;
    }
}
