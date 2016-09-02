package com.ponxu.test.socket5;


import com.ponxu.utils.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xuwenzhao
 * @date 2016-08-26
 */
public class Test2 {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8388);
        Socket client = server.accept();
        new DownThread(client).start();
    }

    static class DownThread extends Thread {
        Socket socket;

        public DownThread(Socket socket) {
            super();
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                while (true) {
                    byte[] buffer = new byte[4096];
                    int len = in.read(buffer);
                    if (len == -1) break;

                    System.out.println("=======================\n"
                            + "len: " + len + "\n"
                            + "string: " + new String(buffer, 0, len) + "\n"
                            + "bytes: " + Utils.bytesToHexString(buffer, 0, len));
                }
            } catch (Exception e) {
            }
        }
    }

    static byte[] decrypt(byte[] data) throws Exception {
        // 'aes-256-cfb': (32, 16, OpenSSLCrypto)
        //

        byte[] key = {};
        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        SecretKeySpec aesSecret = new SecretKeySpec(key, "AES");
        Cipher decipher = Cipher.getInstance("AES/CFB/NoPadding");

        IvParameterSpec ivps = new IvParameterSpec(iv);

        decipher.init(Cipher.DECRYPT_MODE, aesSecret, ivps);
        byte[] deciphered = decipher.doFinal(data);

        return deciphered;
    }
}
