package com.ponxu.test.io;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15-8-13.
 */
public interface Connection {
    public void write(ByteBuffer data);

    public void close();
}
