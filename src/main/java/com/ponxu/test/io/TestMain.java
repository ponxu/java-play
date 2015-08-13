package com.ponxu.test.io;

import java.io.IOException;

/**
 * Created by xwz on 15-8-13.
 */
public class TestMain {
    public static void main(String[] args) throws IOException {
        Server s = new NIOServer(8080);
        s.start();
    }
}
