package com.ponxu.onepiece;

/**
 * Created by xwz on 15-8-13.
 */
public interface Connection {
    public void write(byte[] data);

    public void close();
}
