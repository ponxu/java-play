package com.ponxu.test.thrift;

import com.ponxu.utils.Utils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

/**
 * @author ponxu
 * @date 2016-08-15
 */
public class MyClient {
    public static void main(String[] args) throws Exception {
        TSocket transport = new TSocket("127.0.0.1", 9090);
        TProtocol protocol = new TBinaryProtocol(transport);
        Calculator.Client client = new Calculator.Client(protocol);
        transport.open();

        for (int i = 0; i < 100; i++) {
            new Thread("Test" + i) {
                @Override
                public void run() {
                    for (int k = 0; k < 100; k++) {
                        int ret = 0;
                        try {
                            ret = client.add(1, 2);
                            client.zip();
                        } catch (TException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " " + k + " " + ret);
                        Utils.sleep(10);
                    }
                }
            }.start();
        }
    }
}
