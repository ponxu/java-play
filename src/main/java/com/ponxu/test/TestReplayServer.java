package com.ponxu.test;

import com.ponxu.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ponxu
 * @date 2016-11-16
 */
public class TestReplayServer implements Runnable {
    private static final int SO_TIMEOUT = 30 * 1000;
    private static final int BUFFER_SIZE = 1024 * 4;

    private String bind = "0.0.0.0";
    private int port;
    private String upHost;
    private int upport;

    private boolean isRunning;
    private ServerSocket ss;
    private ThreadPoolExecutor pool;

    public TestReplayServer(int port, String upHost, int upport) {
        this.port = port;
        this.upHost = upHost;
        this.upport = upport;
    }

    public synchronized void startup() throws IOException {
        if (!isRunning) {
            pool = new ThreadPoolExecutor(5, 50, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(200));
            ss = new ServerSocket();
            ss.bind(new InetSocketAddress(bind, port));

            isRunning = true;
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            Socket down = null, up = null;
            try {
                down = ss.accept();
                performSocket(down);

                up = new Socket(upHost, upport);
                performSocket(up);

                byte k = (byte) (up.getLocalPort() % 127);
                if (k == 0) k = 100;
                pool.submit(new RouteWorker(down, up, k));
                pool.submit(new RouteWorker(up, down, k));
            } catch (IOException e) {
                // e.printStackTrace();
                Utils.closeQuietly(down);
                Utils.closeQuietly(up);
            }
        }
    }

    private void performSocket(Socket socket) throws SocketException {
        socket.setTcpNoDelay(true);
        socket.setReuseAddress(true);
        socket.setSoTimeout(SO_TIMEOUT);
    }

    public synchronized void shutdown() throws IOException {
        if (isRunning) {
            isRunning = false;
            ss.close();
            pool.shutdown();
        }
    }

    static class RouteWorker implements Runnable {
        private byte k;
        private Socket from;
        private Socket to;

        public RouteWorker(Socket from, Socket to, byte k) {
            this.from = from;
            this.to = to;
            this.k = k;
        }

        @Override
        public void run() {
            try {
                InputStream in = from.getInputStream();
                OutputStream out = to.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    if (k != 0) {
                        for (int i = 0; i < len; i++) {
                            buffer[i] = (byte) (buffer[i] ^ k);
                        }
                    }
                    out.write(buffer, 0, len);
                }
            } catch (IOException e) {
                // e.printStackTrace();
            } finally {
                Utils.closeQuietly(from);
                Utils.closeQuietly(to);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TestReplayServer rs = new TestReplayServer(1080, "127.0.0.1", 19988);
        rs.startup();
    }
}
