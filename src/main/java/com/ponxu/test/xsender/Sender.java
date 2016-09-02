package com.ponxu.test.xsender;

import com.ponxu.test.xsender.RangeFile.Chunk;
import com.ponxu.test.xsender.RangeFile.Range;
import com.ponxu.utils.Utils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author xuwenzhao
 * @date 2016-08-31
 */
public class Sender {
    static String file = "/home/xwz/下载/zookeeper-3.4.8.tar.gz";
    static String server = "127.0.0.1";
    static int port = 9999;
    static int worker = 5;
    static int retry = 10;

    public static void main(String[] args) throws Exception {
        String usage = "Usage: [-f file] [-s server] [-p port] [-w worker] [-r retry] [-h help]";
        for (int i = 0; i < args.length; i++) {
            // TODO check i
            switch (args[i]) {
                case "-f":
                    file = args[i + 1];
                    break;
                case "-s":
                    server = args[i + 1];
                    break;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    break;
                case "-w":
                    worker = Integer.parseInt(args[i + 1]);
                    break;
                case "-r":
                    retry = Integer.parseInt(args[i + 1]);
                    break;
                case "-h":
                    System.out.println(usage);
                    return;
            }
        }
        System.out.println(String.format("send %s to %s:%d, with %d worker", file, server, port, worker));

        RangeFile rf = new RangeFile(file);

        List<Range> list = rf.getRangeList(worker);
        CountDownLatch cd = new CountDownLatch(list.size());
        for (Range r : list) {
            new SendWorker(file, r, cd).start();
        }
        cd.await();
    }


    static class SendWorker extends Thread {
        private String fileName;
        private Range range;
        private CountDownLatch cd;
        private int okChunkId = -1; // 重试续传

        public SendWorker(String fileName, Range range, CountDownLatch cd) {
            this.fileName = fileName;
            this.range = range;
            this.cd = cd;
        }

        @Override
        public void run() {
            for (int i = 0; i < retry + 1; i++) {
                if (i > 0) {
                    System.out.println(String.format("retry range-%d at %d", range.id, i));
                    Utils.sleep(1000);
                }
                if (doRun()) {
                    break;
                }
            }
        }

        private boolean doRun() {
            Socket socket = null;
            OutputStream out = null;
            DataOutputStream dout = null;
            try {
                socket = new Socket();
                socket.setTcpNoDelay(true);
                socket.setReuseAddress(true);
                socket.setSoTimeout(60000);
                socket.setSoLinger(true, 5);
                socket.setSendBufferSize(1024 * 2);
                socket.setReceiveBufferSize(1024);
                socket.setKeepAlive(true);
                socket.connect(new InetSocketAddress(server, port), 60000);

                System.out.println(String.format("connect to %s %d", server, port));

                out = socket.getOutputStream();
                dout = new DataOutputStream(out);

                // handshake
                dout.writeUTF(fileName);
                dout.writeInt((int) range.af.length());
                dout.flush();

                range.af.seek(range.offset);

                while (true) {
                    Chunk chunk = range.readChunk();
                    if (chunk == null) {
                        break;
                    }

                    if (okChunkId >= chunk.id) {
                        System.out.println("------- " + okChunkId + " " + chunk.id);
                        continue;
                    }

                    // offset length data
                    dout.writeInt(chunk.offset);
                    dout.writeInt(chunk.data.length);
                    dout.write(chunk.data);
                    dout.flush();
                    // chunk ok
                    // System.out.println(">>>>> " + okChunkId + " " + chunk.id);
                    okChunkId = chunk.id;

                    if (chunk.id % 100 == 0) {
                        System.out.println(String.format("sending range-%d %d", range.id, chunk.id));
                    }
                }
                System.out.println(String.format("range-%d finished", range.id));
                return true;
            } catch (Exception e) {
                // TODO fix exception
                e.printStackTrace();
            } finally {
                Utils.closeQuietly(dout);
                Utils.closeQuietly(out);
                Utils.closeQuietly(socket);
                cd.countDown();
            }
            return false;
        }
    }
}
