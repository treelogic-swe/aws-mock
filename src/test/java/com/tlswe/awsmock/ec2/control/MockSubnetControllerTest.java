package com.tlswe.awsmock.ec2.control;


import com.tlswe.awsmock.ec2.model.MockSubnet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockSubnetController.class})
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*" })
public class MockSubnetControllerTest {

    @Test
    public void Test_getInstance() {
        MockSubnetController instance = MockSubnetController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeSubnets returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeSubnet() throws Exception {
        
        MockSubnetController controller = Mockito.spy(MockSubnetController.class);

        Map<String, MockSubnet> allMockSubnet = new ConcurrentHashMap<String, MockSubnet>();
        MockSubnet mockSubnet = new MockSubnet();
        mockSubnet.setSubnetId("s-wewe");
        allMockSubnet.put("s-2323", mockSubnet);
        MockSubnet mockSubnet1 = new MockSubnet();
        mockSubnet1.setSubnetId("s-asdasdasd");
        allMockSubnet.put("s-23223233", mockSubnet1);

        MemberModifier.field(MockSubnetController.class, "allMockSubnets").set(controller,
                allMockSubnet);

        Collection<MockSubnet> collectionOfMockSubnet = controller.describeSubnets();
        
        Collection<MockSubnet> restoreSubnets = new ArrayList<MockSubnet>(collectionOfMockSubnet.size());
        for (MockSubnet mockSubnetRes : collectionOfMockSubnet)
        {
        	restoreSubnets.add(mockSubnetRes);
        }
        int collectionCount = collectionOfMockSubnet.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
        
        for(MockSubnet restoreSubnet : collectionOfMockSubnet) {
        	controller.deleteSubnet(restoreSubnet.getSubnetId());
        }
        
        controller.restoreAllMockSubnet(restoreSubnets);
        
        collectionOfMockSubnet = controller.describeSubnets();

        collectionCount = collectionOfMockSubnet.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createSubnet() throws Exception {
        
       MockSubnet mockSubnet = MockSubnetController
                .getInstance()
                .createSubnet("TestCDR", "VpcId");

    
        Assert.assertNotNull("Internet gateway created.", mockSubnet.getSubnetId());
    }
    
    @Test
    public void Test_deleteSubnet() throws Exception {
        
       MockSubnet mockSubnet = MockSubnetController
                .getInstance()
                .createSubnet("TestCDR", "VpcId");
       MockSubnet mockSubnetDelete = MockSubnetController
               .getInstance()
               .deleteSubnet(mockSubnet.getSubnetId());
    
        Assert.assertNotNull("Internet gateway deleted.", mockSubnetDelete.getSubnetId());
    }
    
    @Test
    public void Test_restoreSubnet() throws Exception {
        
    	Map<String, MockSubnet> allMockSubnet = new ConcurrentHashMap<String, MockSubnet>();
        MockSubnet mockSubnet = new MockSubnet();
        mockSubnet.setSubnetId("s-wewe");
        allMockSubnet.put("s-2323", mockSubnet);
        MockSubnet mockSubnet1 = new MockSubnet();
        mockSubnet1.setSubnetId("s-asdasdasd");
        allMockSubnet.put("s-23223233", mockSubnet1);
       
        MockSubnetController.getInstance()
               .restoreAllMockSubnet(allMockSubnet.values());
    }

}