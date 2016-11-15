package com.ponxu.test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author ponxu
 * @date 2016-11-15
 */
public class TestAES {
    static String AES_ALGORITHM = "AES/ECB/PKCS5Padding";
    // static String AES_ALGORITHM = "AES";

    public static void main(String[] args) throws Exception {
        byte[] key = genarateRandomKey();
        System.out.println("ky:" + Arrays.toString(key) + " len:" + key.length);


        byte[] orign = "hello你好".getBytes();
        System.out.println("on:" + Arrays.toString(orign) + " len:" + orign.length);

        byte[] en = encrypt(orign, key);
        System.out.println("en:" + Arrays.toString(en) + " len:" + en.length);

        byte[] de = decrypt(en, key);
        System.out.println("de:" + Arrays.toString(de) + " len:" + de.length);
        System.out.println(new String(de));
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        // byte[] enCodeFormat = secretKey.getEncoded();
        // SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
        SecretKeySpec seckey = secretKey;

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, seckey);// 初始化
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        // byte[] enCodeFormat = secretKey.getEncoded();
        // SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
        SecretKeySpec seckey = secretKey;

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, seckey);// 初始化
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] genarateRandomKey() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }

    public static byte[] genarateRandomKey2() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx".getBytes());

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128, random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }
}
