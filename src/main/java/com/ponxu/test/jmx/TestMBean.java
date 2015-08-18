package com.ponxu.test.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * Created by xwz on 15-8-18.
 */
public class TestMBean {
    static void putData() throws Exception {
        PersonInfo p1 = new PersonInfo();
        PersonInfo p2 = new PersonInfo();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(p1, new ObjectName("com.ponxu:obj=person1"));
        mbs.registerMBean(p2, new ObjectName("com.ponxu:obj=person2"));
    }

    static void readData() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> onames = mbs.queryNames(new ObjectName("com.ponxu:obj=*"), null);

        for (ObjectName oname : onames) {
            System.out.println(oname);
            MBeanInfo minfo = mbs.getMBeanInfo(oname);
            for (MBeanAttributeInfo attrInfo : minfo.getAttributes()) {
                String attrName = attrInfo.getName();
                Object v = mbs.getAttribute(oname, attrName);
                System.out.println(attrName + " = " + v);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        putData();
        readData();
    }
}
