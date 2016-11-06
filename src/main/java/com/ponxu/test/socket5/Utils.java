package com.ponxu.test.socket5;

import java.nio.ByteBuffer;

/**
 * @author ponxu
 * @date 2016-11-06
 */
public class Utils {
    public static void throwAsRuntime(Exception e) {
        RuntimeException re = new RuntimeException(e.getMessage(), e);
        re.setStackTrace(e.getStackTrace());
        throw re;
    }

    public static boolean isEqual(ByteBuffer buffer, byte[] bytes) {
        boolean is = true;
        for (int i = 0; is && i < bytes.length; i++) {
            is = buffer.get(i) == bytes[i];
        }
        return is;
    }
}
