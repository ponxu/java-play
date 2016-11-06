package com.ponxu.test.socket5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author ponxu
 * @date 2016-11-06
 */
public class Session extends IOLoop.AbsHandler {
    private static final byte VER = 0x5;
    private static final byte AUTH_NONE = 0x0;
    private static final byte[] AUTH_RQ = {VER, AUTH_NONE};
    private static final byte[] AUTH_RS = {VER, AUTH_NONE};
    // ==============
    private static final byte ADDR_IPV4 = 0x1;
    private static final byte ADDR_DOMAIN = 0x3;
    private static final byte ADDR_IPV6 = 0x4;
    // ==============
    private static final byte CMD_CONNECT = 0x1;
    private static final byte CMD_BIND = 0x2;
    private static final byte CMD_UDP_ASSOCIATE = 0x3;
    // ==============
    public static final byte STAGE_AUTH = 0;
    public static final byte STAGE_VER = 1;
    public static final byte STAGE_CONNECT = 2;
    public static final byte STAGE_PIPE = 3;

    private byte stage = STAGE_AUTH;
    private IOLoop downLoop;
    private SocketChannel downChannel;
    private ByteBuffer buffer;
    private IOKeeper keeper;

    public Session(IOLoop downLoop, SocketChannel downChannel) {
        this.downLoop = downLoop;
        this.downChannel = downChannel;
        this.stage = STAGE_AUTH;
        this.keeper = new IOKeeper(downChannel);
    }

    @Override
    public void doRead(SelectionKey key) throws IOException {
        if (buffer == null) {
            buffer = BufferUtils.get();
        }

        switch (stage) {
            case STAGE_AUTH:
                stageAuth();
                break;
            case STAGE_VER:
                stageVer();
                break;
            case STAGE_CONNECT:
                stageConnect();
                break;
            case STAGE_PIPE:
                stagePipe();
                break;
            default:
                System.out.println("Unreachable statement!");
        }
    }

    private void stageAuth() throws IOException {
        keeper.read(buffer, 2, () -> {
            buffer.flip();
            if (Utils.isEqual(buffer, AUTH_RQ)) {
                buffer.clear();
                buffer.put(AUTH_RS);
                keeper.write(buffer, () -> stage = STAGE_CONNECT);
            } else {
                close();
            }
        });
    }

    private void stageVer() {
    }

    private void stageConnect() {
    }

    private void stagePipe() {
    }

    @Override
    public void doWrite(SelectionKey key) throws IOException {
        keeper.write(buffer, null);
    }

    public void close() {
        try {
            downChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BufferUtils.release(buffer);
        }
    }

}
