package com.ponxu.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by xwz on 15-8-5.
 */
public class TestJDKProxy {
    public static void main(String[] args) {
        MyInterface my = (MyInterface) Proxy.newProxyInstance(
                TestJDKProxy.class.getClassLoader(),
                new Class[]{MyInterface.class},
                new MyInvoker());
        my.say();
        System.out.println(my.sum(1, 2, 3));
    }
}

interface MyInterface {
    void say();

    int sum(int... nums);
}

class MyInvoker implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Invoke: " + method.getName() + " args: " + args);
        return 0;
    }
}