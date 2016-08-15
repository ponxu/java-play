package com.ponxu.test.thrift;

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

        int res = client.add(1, 2);
        System.out.println("res:" + res);

        client.zip();
    }
}
