package com.tlswe.awsmock.common.util;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PropertiesUtilsTest {


    private static final String INSTANCE_MIN_SHUTDOWN_TIME_KEY = "instance.min.shutdown.time.seconds";
    private static final String NO_PROPERTY_DEFINED = "no.property.defined";

    /* Test to see if it is possible to get properties from the default properties file*/
    @Test
    public void TestPropertyInString(){
       Assert.assertTrue("6".equals(PropertiesUtils.getProperty(INSTANCE_MIN_SHUTDOWN_TIME_KEY)));
    }


    @Test
    public void TestgetPropertyWithPrefix(){

        String prefixOfProperty = "predefined.mock.ami";
        Collection<String> collection = PropertiesUtils.getPropertiesByPrefix(prefixOfProperty);
        Assert.assertTrue(3==collection.size());
    }

    @Test
    public void TestgetIntFromProperty(){
       Assert.assertTrue(6==PropertiesUtils.getIntFromProperty(INSTANCE_MIN_SHUTDOWN_TIME_KEY));
    }

    @Test
    public void TestgetPropertyNoneDefined(){
       Assert.assertTrue(0==PropertiesUtils.getIntFromProperty(NO_PROPERTY_DEFINED));
       Assert.assertTrue(null==PropertiesUtils.getProperty(NO_PROPERTY_DEFINED));
    }


}
