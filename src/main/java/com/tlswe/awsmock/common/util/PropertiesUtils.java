package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Providing utilities such as properties loading/fetching. Default properties in 'aws-mock-default.properties' will be
 * loaded first. And then if there is a user-defined properties file 'aws-mock.properties', it will also be loaded and
 * properties with same name will be loaded and override those loaded from 'aws-mock-default.properties'.
 *
 * @author xma
 *
 */
public final class PropertiesUtils {

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

    /**
     * Filename for aws-mock-default.properties, containing the default properties if no user-defined
     * aws-mock.properties found in classpath.
     */
    public static final String FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES = "aws-mock-default.properties";

    /**
     * Filename for aws-mock.properties, which will override the same properties previously loaded from
     * aws-mock-default.properties.
     */
    public static final String FILE_NAME_AWS_MOCK_PROPERTIES = "aws-mock.properties";


    /**
     * Constructor is made private as this is a utility class which should always be used in static way.
     */
    private PropertiesUtils() {

    }

    static {
        InputStream inputStream = null;

        // first load default properties from aws-mock-default.properties
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES);
        if (null == inputStream) {
            log.error("properties file '{}' not found!", FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES);
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                log.error("fail to read from '{}' - {}", FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES,
                        e.getMessage());
            }
        }

        // then load user-defined overriding properties from aws-mock.properties if it exists in classpath
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(FILE_NAME_AWS_MOCK_PROPERTIES);
        if (null == inputStream) {
            log.warn(
                    "properties file '{}' not found in classpath, no default property in '{}' will be overridden",
                    FILE_NAME_AWS_MOCK_PROPERTIES, FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES);
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                log.error("fail to read from '{}' - {}", FILE_NAME_AWS_MOCK_PROPERTIES, e.getMessage());
            }
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
