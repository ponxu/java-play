package com.ponxu.test;


import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xuwenzhao
 * @date 2015-10-19
 */
public class TestSocket {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 500; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Socket sc = new Socket("127.0.0.1", 10068);
                        OutputStream out = sc.getOutputStream();
                        while (true) {
                            out.write("hello,world".getBytes());
                            out.flush();
                            System.out.println("==================");
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        Thread.currentThread().join();
    }
}
