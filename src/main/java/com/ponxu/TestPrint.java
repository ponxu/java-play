package com.ponxu;

/**
 * @author ponxu
 * @date 2016-08-29
 */
public class TestPrint {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.print(i);
            Thread.sleep(500);
            System.out.print("\b");
        }
        System.out.println();
    }
}
