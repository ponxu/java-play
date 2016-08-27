package com.ponxu.test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xuwenzhao
 * @date 2015-11-09
 */
public class TestGeneric {

    public void coll(List<Base> list) {
    }

    public void coll2(List<? extends Base> list) {
    }


    public void array(Base[] list) {
    }

    public static void main(String args[]) {
        TestGeneric test = new TestGeneric();
        List<Derived> list = new LinkedList<Derived>();
        // lt.coll(list); // 错
        test.coll2(list);

        // 数值可协变
        Base[] arr = new Derived[10];
        test.array(arr);
    }

}

class Base {
}

class Derived extends Base {
}