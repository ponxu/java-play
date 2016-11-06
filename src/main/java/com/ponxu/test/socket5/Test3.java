package com.ponxu.test.socket5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author ponxu
 * @date 2016-08-27
 */
public class Test3 {
    private static final byte VER = 0x5;
    private static final byte AUTH_NONE = 0x0;

    private static final byte ADDR_IPV4 = 0x1;
    private static final byte ADDR_DOMAIN = 0x3;
    private static final byte ADDR_IPV6 = 0x4;

    private static final byte CMD_CONNECT = 0x1;
    private static final byte CMD_BIND = 0x2;
    private static final byte CMD_UDP_ASSOCIATE = 0x3;

    private static final byte REP_OK = 0x0;
    private static final byte REP_SOCK_FAIL = 0x1;
    private static final byte REP_REFUSE = 0x5;


    public static void main(String[] args) throws IOException {
        byte[] buffer = new byte[1024];
        ServerSocket server = new ServerSocket(1080);
        while (true) {
            Socket local = server.accept();
            System.out.println(local.getLocalAddress());
            System.out.println(local.getLocalPort());
            System.out.println("============");
            System.out.println(local.getInetAddress());
            System.out.println(local.getPort());
            System.out.println("============");
            System.out.println(local.getRemoteSocketAddress());
            System.out.println("============");

            InputStream localIn = local.getInputStream();
            OutputStream localOut = local.getOutputStream();

            // handshake =====================
            int len = localIn.read(buffer);
            printBytes(buffer, 0, len);

            byte[] response = {VER, AUTH_NONE};
            localOut.write(response);
            localOut.flush();
            System.out.printf("shake>>>>>>>>");
            printBytes(response, 0, response.length);

            // cmd============================
            len = localIn.read(buffer);
            printBytes(buffer, 0, len);
            byte cmd = buffer[1];
            System.out.println("cmd:" + cmd);

            String addr = parseAddr(buffer);
            int port = parsePort(buffer, len);
            System.out.println("addr:" + addr + " port:" + port);

            // connect
            Socket remote = new Socket(addr, port);
            response = new byte[len];
            response[0] = VER;
            response[1] = REP_OK; // 00:成功 01:普通的SOCKS服务器请求失败...
            //response[2] = 0x0; // 保留
            System.arraycopy(buffer, 3, response, 3, len - 3);
            localOut.write(response);
            localOut.flush();
            printBytes(response, 0, len);

            // pipe
            new PipeThread(localIn, remote.getOutputStream()).start();
            new PipeThread(remote.getInputStream(), localOut).start();
        }
    }

    public static class PipeThread extends Thread {
        private byte[] temp = new byte[1024];
        private InputStream in;
        private OutputStream out;

        public PipeThread(InputStream in, OutputStream out) {
            super();
            this.in = in;
            this.out = out;
            setDaemon(true);
        }

        @Override
        public void run() {
            int len;
            try {
                while ((len = in.read(temp)) != -1) {
                    out.write(temp, 0, len);
                }
                System.out.println("close.............");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printBytes(byte[] bArray, int from, int len) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = from; i < from + len; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        System.out.println("bytes: " + len + " [" + sb + "]");
    }

    public static String parseAddr(byte[] data) {
        byte type = data[3];
        System.out.println("addr type:" + type);

        switch (type) {
            case ADDR_IPV4:
                // 4 bytes
                return (0xFF & data[4]) + "." + (0xFF & data[5]) + "." + (0xFF & data[6]) + "." + (0xFF & data[7]);
            case ADDR_DOMAIN:
                // var bytes
                int len = data[4];
                return new String(data, 5, len);
            case ADDR_IPV6:
                // 16 bytes
                // TODO
            default:
                return null;
        }
    }

    public static int parsePort(byte[] data, int len) {
        int from = len - 2; // last 2 bytes
        int port = 0;
        for (int i = from; i < len; i++) {
            port <<= 8;
            port += 0xFF & data[i];
        }
        return port;
    }
}
