package com.ponxu.onepiece;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15-8-13.
 */
public interface Connection {
    public void write(byte[] data);
    public void write(ByteBuffer data);

    public void close();
}
