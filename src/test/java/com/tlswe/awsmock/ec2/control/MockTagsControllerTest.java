package com.tlswe.awsmock.ec2.control;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.ec2.model.Tag;
import com.tlswe.awsmock.ec2.model.MockSubnet;
import com.tlswe.awsmock.ec2.model.MockTags;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockTagsController.class})
public class MockTagsControllerTest {

    @Test
    public void Test_getInstance() {
        MockTagsController instance = MockTagsController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeTagss returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeTags() throws Exception {
        
        MockTagsController controller = Mockito.spy(MockTagsController.class);

        List<MockTags> allMockTags = new ArrayList<MockTags>();
        MockTags mockTags = new MockTags();
        List<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        mockTags.setResourcesSet(resources);
        allMockTags.add(mockTags);
        MockTags mockTags1 = new MockTags();
        mockTags1.setResourcesSet(resources);
        allMockTags.add(mockTags1);

        MemberModifier.field(MockTagsController.class, "allMockTags").set(controller,
                allMockTags);

        Collection<MockTags> collectionOfMockTags = controller.describeTags();

        Collection<MockTags> restoreTagss = new ArrayList<MockTags>(collectionOfMockTags.size());
        for (MockTags mockTagsRes : collectionOfMockTags)
        {
        	restoreTagss.add(mockTagsRes);
        }
        int collectionCount = collectionOfMockTags.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
        
        for(MockTags restoreTags : collectionOfMockTags) {
        	controller.deleteTags(restoreTags.getResourcesSet());
        }
        
        controller.restoreAllMockTags(restoreTagss);
        
        collectionOfMockTags = controller.describeTags();

        collectionCount = collectionOfMockTags.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createTags() throws Exception {
       
        List<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("key", "value");
       
        MockTags mockTags = MockTagsController
                .getInstance()
                .createTags(resources, tags);

    
        Assert.assertNotNull("Internet gateway created.", mockTags.getResourcesSet());
    }
    
    @Test
    public void Test_deleteTags() throws Exception {
        List<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("key", "value");
        MockTagsController mockTagsController = MockTagsController.getInstance();
        MockTags mockTags = mockTagsController.createTags(resources, tags);
        Assert.assertTrue("Internet gateway deleted.", mockTagsController.deleteTags(resources));
    }

   @Test
    public void Test_deleteNoTagsCreated() throws Exception {
     
        MockTagsController.getInstance()
               .restoreAllMockTags(null); // to empty the tags list

        List<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("key", "value");
        MockTagsController mockTagsController = MockTagsController.getInstance();

        Assert.assertTrue("Empty list. Cannot delete.", mockTagsController.deleteTags(resources)==false);
    }

    @Test
    public void Test_deleteTagsResourcesNotFound() throws Exception {

        MockTagsController.getInstance()
               .restoreAllMockTags(null); // to empty the tags list

        List<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");

        // Resources not passed to controller
        List<String> resourcesNotAttached = new ArrayList<String>();
        resourcesNotAttached.add("resource3");
        resourcesNotAttached.add("resource4");
        
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("key", "value");
        MockTagsController mockTagsController = MockTagsController.getInstance();
        MockTags mockTags = mockTagsController.createTags(resources, tags);
        Assert.assertTrue("Empty list. Cannot delete.", mockTagsController.deleteTags(resourcesNotAttached)==false);
    }

    @Test
    public void Test_restoreTags() throws Exception {
        
    	 List<MockTags> allMockTags = new ArrayList<MockTags>();
         MockTags mockTags = new MockTags();
         List<String> resources = new ArrayList<String>();
         resources.add("resource1");
         resources.add("resource2");
         mockTags.setResourcesSet(resources);
         allMockTags.add(mockTags);
         MockTags mockTags1 = new MockTags();
         mockTags1.setResourcesSet(resources);
         allMockTags.add(mockTags1);
       
         MockTagsController.getInstance()
               .restoreAllMockTags(allMockTags);
    }

    @Test
    public void Test_restoreTagsNull() throws Exception {
         MockTagsController.getInstance()
               .restoreAllMockTags(null);
         Assert.assertTrue("No tags restored", MockTagsController.getInstance().describeTags().size()==0);
    }
}
