package com.ponxu.test.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * thrift -gen java -out src/main/java src/main/java/com/ponxu/test/thrift/calculator.thrift
 *
 * @author ponxu
 * @date 2016-08-15
 */
public class MyServer {
    public static class CalculatorImpl implements Calculator.Iface {
        @Override
        public int add(int num1, int num2) throws TException {
            return num1 + num2;
        }

        @Override
        public void zip() throws TException {
            System.out.println("zip........");
        }
    }

    public static void main(String[] args) {
        try {
            TProcessor processor = new Calculator.Processor<Calculator.Iface>(new CalculatorImpl());

            TServerTransport serverTransport = new TServerSocket(9090);
            // TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
