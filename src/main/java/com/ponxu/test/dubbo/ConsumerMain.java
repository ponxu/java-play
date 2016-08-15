package com.ponxu.test.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author ponxu
 * @date 2016-08-15
 */
public class ConsumerMain {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"test-dubbo-consumer.xml"});
        context.start();

        DemoService demoService = (DemoService) context.getBean("demoService");
        int ret = demoService.add(1, 2);
        System.out.println(ret);

        context.close();
    }

}
