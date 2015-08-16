package com.ponxu.onepiece;

import java.nio.ByteBuffer;

/**
 * Created by xwz on 15-8-14.
 */
public interface Handler {
    public void onConnected(Connection conn);

    public void onRead(Connection conn, Object receivedData);

    public void onClosed(Connection conn);

    public void onError(Connection conn, Throwable cause);
}
