package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Providing utilities such as properties loading/fetching.
 *
 * @author xma
 *
 */
public final class PropertiesUtils {

    /**
     * Constructor is made private as this is a utility class which should be always used in static way.
     */
    private PropertiesUtils() {

    }

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(PropertiesUtils.class);
    // @InjectLogger
    // private static Logger _log;

    /**
     * All properties loaded into aws-mock.properties.
     */
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("aws-mock.properties"));
        } catch (IOException e) {
            log.error("fail to load 'aws-mock.properties' - {}", e.getMessage());
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
        return properties.getProperty(propertyName);
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
        Set<Object> keys = properties.keySet();
        for (Object key : keys) {
            if (null != key && key.toString().startsWith(propertyNamePrefix)) {
                ret.add(properties.getProperty(key.toString()));
            }
        }

        return ret;
    }

}
