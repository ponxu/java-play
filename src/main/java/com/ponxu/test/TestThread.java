package com.ponxu.test;

/**
 * @author xuwenzhao
 * @date 2015-10-22
 */
public class TestThread {
    public static void main(String[] args) {
        // user-thread不结束, jvm不退出
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("user thread..");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        System.out.println("main thread over...");
    }
}
