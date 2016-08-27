package com.ponxu.test;

/**
 * 进制
 *
 * @author xuwenzhao
 * @date 2015-12-08
 */
public class TestNum {
    public static void main(String[] args) {
        System.out.println(String.format("%02x", 17));
        System.out.println(String.format("%02x%02x%02x", 180, 175, 175).toUpperCase());
    }
}
