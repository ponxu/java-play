package com.ponxu.onepiece.nio;

import com.ponxu.onepiece.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by xwz on 15-8-13.
 */
public class NIOConnection implements Connection {
    private SocketChannel channel;

    public NIOConnection(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 64);
        buffer.put(data);
        buffer.flip();
        write(buffer);
    }

    @Override
    public void write(ByteBuffer data) {
        try {
            channel.write(data);
            System.out.println("write:" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }
}
