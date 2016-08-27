package com.ponxu.test.dubbo;

import com.ponxu.utils.Utils;
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
        for (int i = 0; i < 100; i++) {
            new Thread("Test" + i) {
                @Override
                public void run() {
                    for (int k = 0; k < 100; k++) {
                        int ret = demoService.add(1, 2);
                        System.out.println(Thread.currentThread().getName() + " " + k + " " + ret);
                        Utils.sleep(10);
                    }
                }
            }.start();
        }
    }

}
