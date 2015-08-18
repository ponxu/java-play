package com.ponxu.test.jmx;

/**
 * Created by xwz on 15-8-18.
 */
public class PersonInfo implements IPersonInfoMXBean {

    public String getName() {
        return "Tom" + System.currentTimeMillis();
    }

    public int getAge() {
        return 100;
    }
}