package com.ponxu.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ponxu
 * @date 2016-08-15
 */
public class Utils {
    public static void writeInt(OutputStream out, int i) throws IOException {
        writeBytes(out, intToBytes(i));
    }

    public static void writeString(OutputStream out, String str) throws IOException {
        byte[] bytes = str.getBytes();
        writeBytes(out, intToBytes(bytes.length));
        writeBytes(out, bytes);
    }

    public static void writeBytes(OutputStream out, byte[] bytes) throws IOException {
        // write limit here
        out.write(bytes);
    }

    public static void readBytes(InputStream in, byte[] buffer, int size) throws IOException {
        // read limit here
        if (buffer.length < size) {
            throw new IOException("buffer is too small " + buffer.length + " " + size);
        }

        // 0
        in.read(buffer, 0, size);

        // 1
//        for (int i = 0; i < size; ) {
//            byte b = (byte) in.read();
//            if (b == -1) {
//                throw new IOClosed();
//            }
//            buffer[i] = b;
//            i++;
//        }
    }

    public static int readInt(InputStream in, byte[] buffer) throws IOException {
        readBytes(in, buffer, 4);
        return bytesToInt(buffer);
    }

    public static String readString(InputStream in, byte[] buffer) throws IOException {
        int i = readInt(in, buffer);
        readBytes(in, buffer, i);
        return new String(buffer, 0, i);
    }

    // 高位优先
    public static int bytesToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToBytes(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
        }

    }

    public static void closeQuietly(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException var2) {
        }

    }

    public static void closeQuietly(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException var2) {
        }

    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] bArray, int begin, int end) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = begin; i < end; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }
}
