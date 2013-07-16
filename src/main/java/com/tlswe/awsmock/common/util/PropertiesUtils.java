package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesUtils {

    static private Properties _properties = new Properties();

    static {
        try {
            _properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("aws-mock.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getProperty(final String propertyName) {
        return _properties.getProperty(propertyName);
    }

    public static Set<String> getPropertiesByPrefix(final String propertyNamePrefix) {

        Set<String> ret = new TreeSet<String>();
        Set<Object> keys = _properties.keySet();
        for (Object key : keys) {
            if (null != key && key.toString().startsWith(propertyNamePrefix)) {
                ret.add(_properties.getProperty(key.toString()));
            }
        }

        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(getProperty("ec2.instance.class"));
    }

}
