package com.ponxu.test.dubbo;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * @author ponxu
 * @date 2016-08-15
 */
public class ProviderMain {
        public static void main(String[] args) throws Exception {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"test-dubbo-provider.xml"});
            context.start();
            System.in.read();
        }

}
