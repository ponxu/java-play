package com.ponxu.test.socket5;

import java.nio.ByteBuffer;

/**
 * TODO pool
 *
 * @author ponxu
 * @date 2016-11-06
 */
public class BufferUtils {
    static final int BUFFER_SIZE = 4 * 1024;

    public static ByteBuffer get() {
        return ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public static void release(ByteBuffer buffer) {
    }
}
