package com.ponxu.test.xsender;

import com.ponxu.utils.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xuwenzhao
 * @date 2016-08-31
 */
public class Receiver {
    static String dataTemp = "/tmp";
    static int port = 9999;

    public static void main(String[] args) throws IOException {
        String usage = "Usage: [-t data_tmp] [-p port] [-h help]";
        for (int i = 0; i < args.length; i++) {
            // TODO check i
            switch (args[i]) {
                case "-t":
                    dataTemp = args[i + 1];
                    break;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    break;
                case "-h":
                    System.out.println(usage);
                    return;
            }
        }

        ServerSocket server = new ServerSocket(port);
        System.out.println("listen to: " + port + "  tmp: " + dataTemp);
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
            DataInputStream din = null;
            RandomAccessFile af = null;

            byte[] buffer = new byte[RangeFile.CHUNK_SIZE];
            try {
                in = socket.getInputStream();
                din = new DataInputStream(in);

                // handshake TODO auth
                String fileName = din.readUTF();
                int fileLength = din.readInt();
                System.out.println(fileName + " " + fileLength);

                af = getLocalTempFile(fileName, fileLength);
                int fileOffest = -1;

                while (true) {
                    int offset = din.readInt();
                    int dataLen = din.readInt();
                    din.read(buffer, 0, dataLen);

                    // write file
                    if (offset != fileOffest) {
                        af.seek(offset);
                        System.out.println("seek file to: " + offset);
                        fileOffest = offset;
                    }
                    af.write(buffer, 0, dataLen);

                    fileOffest += dataLen;
                }
            } catch (EOFException e) {
                // pass
            } catch (Exception e) {
                // TODO fix exception
                e.printStackTrace();
            } finally {
                Utils.closeQuietly(af);
                Utils.closeQuietly(din);
                Utils.closeQuietly(in);
                Utils.closeQuietly(socket);
            }
        }
    }

    private static synchronized RandomAccessFile getLocalTempFile(String remoteFileName, int length) throws IOException {
        String name = new File(remoteFileName).getName();
        String tempPath = dataTemp + "/" + name;

        File f = new File(tempPath);
        if (!f.exists()) {
            f.createNewFile();

            RandomAccessFile af = new RandomAccessFile(f, "rw");
            af.setLength(length);
            af.close();

            System.out.println("new file: " + tempPath);
        }

        return new RandomAccessFile(f, "rw");
    }
}
