package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    static private Properties properties = new Properties();

    static {
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("aws-mock.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
       System.out.println(getProperty("ec2.instance.class"));
    }

}
