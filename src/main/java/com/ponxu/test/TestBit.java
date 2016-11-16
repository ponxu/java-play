package com.ponxu.test;

/**
 * @author xuwenzhao
 * @date 2016-11-16
 */
public class TestBit {
    public static void main(String[] args) {
        byte key = 0;
        byte a = 5;

        byte b = (byte) (a ^ key);
        byte r = (byte) (b ^ key);

        System.out.println(a + " " + b + " " + r);
    }

}
