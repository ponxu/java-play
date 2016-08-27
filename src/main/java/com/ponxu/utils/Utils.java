package com.ponxu.utils;

/**
 * @author ponxu
 * @date 2016-08-15
 */
public class Utils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
