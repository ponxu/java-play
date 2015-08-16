package com.ponxu.onepiece.codec;

import com.ponxu.onepiece.Connection;

import java.nio.ByteBuffer;

/**
 * decode read data
 * Created by xwz on 15/8/15.
 */
public interface Decoder {
    public Object decode(Connection conn, ByteBuffer buffer);
}
