package com.ponxu.test.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * Created by xwz on 15-8-20.
 */
public class TestByBuf {
    public static void main(String[] args) {
        System.out.println(Unpooled.buffer());
        System.out.println(Unpooled.directBuffer());

        System.out.println(ByteBufAllocator.DEFAULT.buffer());
        System.out.println(ByteBufAllocator.DEFAULT.directBuffer());
    }
}
