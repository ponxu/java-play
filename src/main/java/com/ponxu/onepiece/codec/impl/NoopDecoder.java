package com.ponxu.onepiece.codec.impl;

import com.ponxu.onepiece.Connection;
import com.ponxu.onepiece.codec.Decoder;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15/8/16.
 */
public class NoopDecoder implements Decoder {
    @Override
    public Object decode(Connection conn, ByteBuffer buffer) {
        return buffer;
    }
}
