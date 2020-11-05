package com.tlswe.awsmock.ec2.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;
import com.tlswe.awsmock.ec2.cxf_generated.RunInstancesResponseType;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEC2QueryHandler.class, PropertiesUtils.class })
public class JAXBUtilTest {
    private static final String SUBNET_ID = "subnetId";

    @Test
    public void Test_marshall() throws Exception {

        String imageID = "ami-1";
        String instanceType = InstanceType.C1_MEDIUM.getName();
        int minCount = 1;
        int maxCount = 1;

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        RunInstancesResponseType runInstancesResponseType = Whitebox.invokeMethod(handler,
                "runInstances", imageID,
                instanceType, minCount, maxCount, SUBNET_ID);

        String xml = JAXBUtil.marshall(runInstancesResponseType, "RunInstancesResponse",
                "2012-02-10");

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<imageId>ami-1</imageId>"));
        Assert.assertTrue(xml.contains("<instanceType>c1.medium</instanceType>"));

    }

    @Test(expected = AwsMockException.class)
    public void Test_marshallFailed() throws Exception {

        // A class not made to be marshaled
        class Person {

            String name;
        }

        JAXBUtil.marshall(new Person(), "Person", "2012-02-10");
    }

    @Test
    public void Test_mashallElasticFoxTrueVersionNull() throws Exception {

        String imageID = "ami-1";
        String instanceType = InstanceType.C1_MEDIUM.getName();
        int minCount = 1;
        int maxCount = 1;

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        RunInstancesResponseType runInstancesResponseType = Whitebox.invokeMethod(handler,
                "runInstances", imageID,
                instanceType, minCount, maxCount, SUBNET_ID);

        String xml = JAXBUtil.marshall(runInstancesResponseType, "RunInstancesResponse", null);

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<imageId>ami-1</imageId>"));
        Assert.assertTrue(xml.contains("<instanceType>c1.medium</instanceType>"));
    }

    @Test
    public void Test_mashallNotElasticFox() throws Exception {

        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                .thenReturn("false");

        String imageID = "ami-1";
        String instanceType = InstanceType.C1_MEDIUM.getName();
        int minCount = 1;
        int maxCount = 1;

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        RunInstancesResponseType runInstancesResponseType = Whitebox.invokeMethod(handler,
                "runInstances", imageID,
                instanceType, minCount, maxCount, SUBNET_ID);

        String xml = JAXBUtil.marshall(runInstancesResponseType, "RunInstancesResponse", null);

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<imageId>ami-1</imageId>"));
        Assert.assertTrue(xml.contains("<instanceType>c1.medium</instanceType>"));
    }

    @Test
    public void Test_mashallReplaceVersionWithElasticFoxVersion() throws Exception {

        String imageID = "ami-1";
        String instanceType = InstanceType.C1_MEDIUM.getName();
        int minCount = 1;
        int maxCount = 1;

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        RunInstancesResponseType runInstancesResponseType = Whitebox.invokeMethod(handler,
                "runInstances", imageID,
                instanceType, minCount, maxCount, SUBNET_ID);

        String xml = JAXBUtil.marshall(runInstancesResponseType, "RunInstancesResponse",
                PropertiesUtils
                        .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX));

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("xmlns:ns3=\"http://ec2.amazonaws.com/doc/" + PropertiesUtils
                .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX)));
        Assert.assertTrue(xml.contains("<imageId>ami-1</imageId>"));
        Assert.assertTrue(xml.contains("<instanceType>c1.medium</instanceType>"));
    }

}
