package com.tlswe.awsmock.ec2.control;


import com.tlswe.awsmock.ec2.model.MockSecurityGroup;
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
@PrepareForTest({ MockSecurityGroupController.class})
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*" })
public class MockSecurityGroupControllerTest {

    @Test
    public void Test_getInstance() {
    	MockSecurityGroupController instance = MockSecurityGroupController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeSecurityGroups returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeSecurityGroup() throws Exception {
        
    	MockSecurityGroupController controller = Mockito.spy(MockSecurityGroupController.class);

        Map<String, MockSecurityGroup> allMockMockSecurityGroup = new ConcurrentHashMap<String, MockSecurityGroup>();
        MockSecurityGroup mockSecurityGroup = new MockSecurityGroup();
        mockSecurityGroup.setGroupId("s-wewe");
        allMockMockSecurityGroup.put("s-2323", mockSecurityGroup);
        MockSecurityGroup mockSecurityGroup1 = new MockSecurityGroup();
        mockSecurityGroup1.setGroupId("s-asdasdasd");
        allMockMockSecurityGroup.put("s-23223233", mockSecurityGroup1);

        MemberModifier.field(MockSecurityGroupController.class, "allMockSecurityGroup").set(controller,
        		allMockMockSecurityGroup);

        Collection<MockSecurityGroup> collectionOfMockSecurityGroup = controller.describeSecurityGroups();
        
        Collection<MockSecurityGroup> restoreMockSecurityGroup = new ArrayList<MockSecurityGroup>(collectionOfMockSecurityGroup.size());
        for (MockSecurityGroup mockSecurityGroupRes : collectionOfMockSecurityGroup)
        {
        	restoreMockSecurityGroup.add(mockSecurityGroupRes);
        }
        int collectionCount = collectionOfMockSecurityGroup.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
        
        for(MockSecurityGroup restoreSg : collectionOfMockSecurityGroup) {
        	controller.deleteSecurityGroup(restoreSg.getGroupId());
        }
        
        controller.restoreAllMockSecurityGroup(restoreMockSecurityGroup);
        
        collectionOfMockSecurityGroup = controller.describeSecurityGroups();

        collectionCount = collectionOfMockSecurityGroup.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }

    @Test
    public void Test_createSecurityGroup() throws Exception {
        MockSecurityGroup mockSecurityGroup = MockSecurityGroupController
                .getInstance()
                .createSecurityGroup("SGName", "Desc", "VpcId");
        Assert.assertNotNull("Internet gateway created.", mockSecurityGroup.getGroupId());
    }

    @Test
    public void Test_deleteSecurityGroup() throws Exception {
       MockSecurityGroup mockSecurityGroup = MockSecurityGroupController
                 .getInstance()
                 .createSecurityGroup("SGName", "Desc", "VpcId");
       MockSecurityGroup mockSubnetDelete = MockSecurityGroupController
               .getInstance()
               .deleteSecurityGroup(mockSecurityGroup.getGroupId());

        Assert.assertNotNull("Internet gateway deleted.", mockSubnetDelete.getGroupId());
    }
    
    @Test
    public void Test_restoreSecurityGroup() throws Exception {
        
    	 Map<String, MockSecurityGroup> allMockMockSecurityGroup = new ConcurrentHashMap<String, MockSecurityGroup>();
         MockSecurityGroup mockSecurityGroup = new MockSecurityGroup();
         mockSecurityGroup.setGroupId("s-wewe");
         allMockMockSecurityGroup.put("s-2323", mockSecurityGroup);
         MockSecurityGroup mockSecurityGroup1 = new MockSecurityGroup();
         mockSecurityGroup1.setGroupId("s-asdasdasd");
         allMockMockSecurityGroup.put("s-23223233", mockSecurityGroup1);
       
         MockSecurityGroupController.getInstance()
               .restoreAllMockSecurityGroup(allMockMockSecurityGroup.values());
    }

}