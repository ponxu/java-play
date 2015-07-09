package com.ponxu.test;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xwz on 15-7-9.
 */
public class TestZooKeeper {

    public static void main(String[] args) throws Exception {
        CountDownLatch connectedLatch = new CountDownLatch(1);

        ZooKeeper zk = new ZooKeeper("192.168.6.158:2181", 5000, (event) -> {
            System.out.println("Type: " + event.getType());
            System.out.println("State: " + event.getState());

            if (event.getState() == KeeperState.SyncConnected) {
                connectedLatch.countDown();
            }
        });

        if (ZooKeeper.States.CONNECTING == zk.getState()) {
            System.out.println("waiting....");
            connectedLatch.await();
            System.out.println("end wait");
        }

        String path = "/xwz_test";

        if (zk.exists(path, false) != null) {
            zk.delete(path, -1);
            System.out.println("Delete: ");
        }

        String rs = zk.create(path, "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Create: " + rs);

        zk.getChildren("/", false).stream().forEach(System.out::println);
    }

}