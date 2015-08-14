package com.ponxu.onepiece;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15-8-14.
 */
public interface Handler {
    public void onConected(Connection conn);

    public void onRead(Connection conn, ByteBuffer buffer);

    public void onClose(Connection conn);

    public void onError(Connection conn, Throwable cause);
}
