package com.ponxu.onepiece.codec.impl;

import com.ponxu.onepiece.Connection;
import com.ponxu.onepiece.codec.Encoder;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15/8/16.
 */
public class NoopEncoder implements Encoder {
    @Override
    public ByteBuffer encode(Connection conn, Object obj) {
        return (ByteBuffer) obj;
    }
}
