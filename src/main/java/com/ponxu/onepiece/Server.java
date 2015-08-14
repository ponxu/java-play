package com.ponxu.onepiece;

import java.io.IOException;

/**
 * Created by xwz on 15-8-13.
 */
public interface Server {
    public void start() throws IOException;

    public void stop() throws IOException;
}
