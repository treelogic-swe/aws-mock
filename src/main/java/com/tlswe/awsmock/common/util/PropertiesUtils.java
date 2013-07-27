package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Providing utilities such as properties loading/fetching.
 * 
 * @author xma
 * 
 */
public class PropertiesUtils {

    /**
     * all properties loaded into aws-mock.properties
     */
    static private Properties _properties = new Properties();

    static {
        try {
            _properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("aws-mock.properties"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get property value by name.
     * 
     * @param propertyName
     *            name of the property to get
     * @return the value
     */
    public static String getProperty(final String propertyName) {
        return _properties.getProperty(propertyName);
    }

    /**
     * Get a set of properties those share the same given name prefix.
     * 
     * @param propertyNamePrefix
     *            prefix of name
     * @return the set of values whose property names share the same prefix
     */
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

}
