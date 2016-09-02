package com.ponxu.test;

import java.nio.ByteBuffer;

/**
 * @author xuwenzhao
 * @date 2016-09-02
 */
public class TestBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int limit = 10;
        buffer.limit(limit);
        for (byte i = 0; buffer.hasRemaining(); i++) {
            buffer.put(i);
            System.out.println("put " + i + " " + buffer.hasRemaining());
        }

        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.println("get " + buffer.get() + " " + buffer.hasRemaining());
        }
    }
}
