package com.ponxu.test.io;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwz on 15-8-14.
 */
public class TestOnepieceClient {
    public static void main(String[] args) throws Exception {
        List<Socket> sockets = new ArrayList<>();
        for (int i = 0; i < 200000; i++) {
            Socket s = new Socket("127.0.0.1", 8080);
            System.out.println(i + " " + s);
            sockets.add(s);
        }
        Thread.currentThread().join();
    }
}
