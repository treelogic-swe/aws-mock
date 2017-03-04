package com.tlswe.awsmock.ec2.control;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tlswe.awsmock.ec2.model.MockVolume;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockVolumeController.class})
public class MockVolumeControllerTest {

    @Test
    public void Test_getInstance() {
        MockVolumeController instance = MockVolumeController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeVolumes returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeVolume() throws Exception {
        
        MockVolumeController controller = Mockito.spy(MockVolumeController.class);

        Map<String, MockVolume> allMockVolume = new ConcurrentHashMap<String, MockVolume>();
        MockVolume mockVolume = new MockVolume();
        allMockVolume.put("i-2323", mockVolume);
        MockVolume mockVolume1 = new MockVolume();
        allMockVolume.put("i-23223233", mockVolume1);

        MemberModifier.field(MockVolumeController.class, "allMockVolumes").set(controller,
                allMockVolume);

        Collection<MockVolume> collectionOfMockVolume = controller.describeVolumes();

        int collectionCount = collectionOfMockVolume.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createVolume() throws Exception {
        
       MockVolume mockVolume = MockVolumeController
                .getInstance()
                .createVolume("TestCDR", "Deafult", "11", 12, "23");
        Assert.assertNotNull("Internet gateway created.", mockVolume.getVolumeId());
    }
    
    @Test
    public void Test_deleteVolume() throws Exception {
        
       MockVolume mockVolume = MockVolumeController
                .getInstance()
                .createVolume("TestCDR", "Deafult", "11", 12, "23");
       MockVolume mockVolumeDelete = MockVolumeController
               .getInstance()
               .deleteVolume(mockVolume.getVolumeId());
    
        Assert.assertNotNull("Internet gateway deleted.", mockVolumeDelete.getVolumeId());
    }

}