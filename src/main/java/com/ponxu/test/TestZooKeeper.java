package com.ponxu.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by xwz on 15-7-9.
 */
public class TestZooKeeper {
    public static void main(String[] args) throws IOException {
        ZooKeeper zk = new ZooKeeper("192.168.6.158:2181", 5000, new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println(event.getState());
            }
        });
    }
}