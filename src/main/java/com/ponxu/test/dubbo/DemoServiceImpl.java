package com.ponxu.test.dubbo;

/**
 * @author ponxu
 * @date 2016-08-15
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public int add(int a, int b) {
        int c = a + b;
        System.out.println("a + b ===> " + c);
        return c;
    }
}
