package com.ponxu.test;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Created by xwz on 15-8-6.
 */
public class TestConfiguration {
    public static void main(String[] args) throws ConfigurationException {
        Configuration conf = new XMLConfiguration("test_config.xml");

        System.out.println(conf.getString("description"));
        System.out.println(conf.getString("db(0).url"));
    }
}
