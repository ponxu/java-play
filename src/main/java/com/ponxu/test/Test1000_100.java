package com.ponxu.test;

/**
 * @author xwz
 * @date 2015-11-26
 */
public class Test1000_100 {
    public static void main(String[] args) {
        Integer a = 1000;
        Integer b = 1000;
        int i = 1000;
        System.out.println(a == b);
        System.out.println(a == i);

        Integer c = new Integer(100), d = new Integer(100);
        System.out.println(c == d);
    }
}
