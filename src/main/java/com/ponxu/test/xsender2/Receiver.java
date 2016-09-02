package com.ponxu.test.xsender2;

import com.ponxu.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xuwenzhao
 * @date 2016-09-01
 */
public class Receiver {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(19999);

        while (true) {
            Socket socket = server.accept();
            new ReceiverWorker(socket).start();
        }
    }

    static class ReceiverWorker extends Thread {
        private Socket socket;

        public ReceiverWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStream in = null;
            FileOutputStream fout = null;
            try {
                in = socket.getInputStream();
                fout = new FileOutputStream("/tmp/test.xsender");

                int len;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                    System.out.println(len);
                }
                System.out.println("client closed");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Utils.closeQuietly(fout);
                Utils.closeQuietly(in);
                Utils.closeQuietly(socket);
            }
        }
    }
}
