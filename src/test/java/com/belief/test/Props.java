package com.belief.test;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class Props {

    @Test
    public void test() {
        Properties props = System.getProperties();
        Enumeration<Object> eles = props.elements();
        while (eles.hasMoreElements()) {
            Object ele = eles.nextElement();
            System.out.println(ele);
        }
        System.out.println("-----------");
        Set<Entry<Object, Object>> set = props.entrySet();
        for (Entry<Object, Object> entry : set) {
            System.out.println(entry.getKey() + "--->" + entry.getValue());

        }
        System.out.println("-----------");
        String basedir = System.getProperty("basedir", System.getProperty("user.dir"));

        System.out.println(basedir);
    }
}
