package com.ponxu.onepiece.codec;

import com.ponxu.onepiece.Connection;

/**
 * Created by xwz on 15/8/15.
 */
public interface Encoder {
    public Object encode(Connection conn, Object obj);
}
