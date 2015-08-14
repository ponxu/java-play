package com.ponxu.test.io;

import com.ponxu.onepiece.Server;
import com.ponxu.onepiece.nio.NIOServer;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by xwz on 15-8-13.
 */
public class TestOnepiece {
    public static void main(String[] args) throws IOException {
        Server s = new NIOServer(8080);
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
