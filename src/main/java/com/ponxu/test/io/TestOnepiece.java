package com.ponxu.test.io;

import com.ponxu.onepiece.Connection;
import com.ponxu.onepiece.Handler;
import com.ponxu.onepiece.Server;
import com.ponxu.onepiece.nio.NIOServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * Created by xwz on 15-8-13.
 */
public class TestOnepiece {
    public static void main(String[] args) throws IOException {
        Server s = new NIOServer().port(8080).handler(new EchoHandler()).build();

        s.start();
        System.out.println("listen on 8080");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            System.out.println(line);
            if ("q".equalsIgnoreCase(line)) {
                s.stop();
                // break;
            }
        }
    }
}


class EchoHandler implements Handler {
    @Override
    public void onConnected(Connection conn) {
        conn.write("welcome\n".getBytes());
    }

    @Override
    public void onRead(Connection conn, Object receivedData) {
        conn.write((ByteBuffer) receivedData);
    }

    @Override
    public void onClosed(Connection conn) {

    }

    @Override
    public void onError(Connection conn, Throwable cause) {

    }
}