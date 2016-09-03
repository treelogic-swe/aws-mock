package com.tlswe.awsmock.common.util;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesUtilsTest {


    private static final String INSTANCE_MIN_SHUTDOWN_TIME_KEY = "instance.min.shutdown.time.seconds";
    private static final String NO_PROPERTY_DEFINED = "no.property.defined";

    /* Test to see if it is possible to get properties from the default properties file*/
    @Test
    public void Test_getPropertyString(){
       Assert.assertTrue("6".equals(PropertiesUtils.getProperty(INSTANCE_MIN_SHUTDOWN_TIME_KEY)));
    }


    @Test
    public void Test_getPropertyWithPrefix(){

        String prefixOfProperty = "predefined.mock.ami";
        Collection<String> collection = PropertiesUtils.getPropertiesByPrefix(prefixOfProperty);
        Assert.assertTrue(3==collection.size());
    }

    @Test
    public void Test_getIntFromProperty(){
       Assert.assertTrue(6==PropertiesUtils.getIntFromProperty(INSTANCE_MIN_SHUTDOWN_TIME_KEY));
    }

    @Test
    public void Test_getPropertyNoneDefined(){
       Assert.assertTrue(0==PropertiesUtils.getIntFromProperty(NO_PROPERTY_DEFINED));
       Assert.assertTrue(null==PropertiesUtils.getProperty(NO_PROPERTY_DEFINED));
    }


}
