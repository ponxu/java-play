package com.ponxu.socket5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author xuwenzhao
 * @date 2016-11-04
 */
public class Session {
    public static final int STAGE_AUTH = 1;
    public static final int STAGE_VAR = 2;
    public static final int STAGE_CONNECT = 3;
    public static final int STAGE_PIPE = 4;

    private static final byte[] VER_RQ = {0x5, 0x1, 0x0};
    private static final byte[] VER_RS = {0x5, 0x0};
    private static final byte[] CONNECT_RQ = {0x5, 0x1, 0x0, 0x1};

    private int stage = STAGE_VAR;
    private ByteBuffer buffer;
    private SocketChannel down;
    private IOKeeper downKeeper;

    public Session(SocketChannel down) {
        this.down = down;
        this.buffer = ByteBuffer.allocate(4 * 1024);
        this.downKeeper = new IOKeeper(this.down, this.buffer);
    }

    public void processRead() throws IOException {
        switch (stage) {
            case STAGE_VAR:
                stageVar();
                break;
            case STAGE_CONNECT:
                stageConnect();
                break;
            case STAGE_PIPE:
                stagePiple();
                break;
            default:
                System.out.println("Unreachable...");
        }
    }

    private void stageVar() throws IOException {
        if (downKeeper.read(3)) {
            buffer.flip();

            boolean isVer = isEquel(buffer, VER_RQ);
            if (isVer) {
                System.out.println("ver is ok");
                write(VER_RS);
                stage = STAGE_CONNECT;
            } else {
                throw new IllegalStateException("ver check fail");
            }
        }
    }

    private void stageConnect() throws IOException {
        if (downKeeper.read(10)) {
            buffer.flip();

            boolean isConnect = isEquel(buffer, CONNECT_RQ);
            if (isConnect) {
                String ip = findHost(buffer, 4, 7);
                int port = findPort(buffer, 8, 9);
                System.out.println("to connct>> " + ip + ":" + port);
            } else {
                throw new IllegalStateException("connect check fail");
            }
        }
    }

    private void stagePiple() {
    }

    private void write(byte[] bytes) throws IOException {
        // TODO deal write full
        buffer.clear();
        buffer.put(VER_RS);
        down.write(buffer);
    }

    private boolean isEquel(ByteBuffer buffer, byte[] bytes) {
        boolean is = true;
        for (int i = 0; is && i < bytes.length; i++) {
            is = bytes[i] == buffer.get(i);
        }
        return is;
    }

    public static String findHost(ByteBuffer buffer, int begin, int end) {
        StringBuffer sb = new StringBuffer();
        for (int i = begin; i <= end; i++) {
            sb.append(Integer.toString(0xFF & buffer.get(i)));
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static int findPort(ByteBuffer buffer, int begin, int end) {
        int port = 0;
        for (int i = begin; i <= end; i++) {
            port <<= 16;
            port += buffer.get(i);
        }
        return port;
    }
}
