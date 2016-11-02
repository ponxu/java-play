package com.ponxu.test;

import com.ponxu.utils.Utils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Created by ponxu on 16-9-5.
 */
public class TestApacheFileMonitor {
    public static void main(String[] args) throws Exception {
        FileAlterationObserver observer = new FileAlterationObserver("/tmp/test", FileFilterUtils.suffixFileFilter(".txt"));
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                System.out.println(file + " create");
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println(file + " delete");
            }

            @Override
            public void onDirectoryCreate(File directory) {
                System.out.println(directory + " create");
            }

            @Override
            public void onDirectoryDelete(File directory) {
                System.out.println(directory + " delete");
            }
        });


        FileAlterationMonitor monitor = new FileAlterationMonitor(100);
        monitor.addObserver(observer);
        monitor.start();
        Utils.sleep(600000);
        monitor.stop();
    }
}
