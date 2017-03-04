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

import com.tlswe.awsmock.ec2.model.MockInternetGateway;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockInternetGatewayController.class})
public class MockInternetGatewayControllerTest {

    @Test
    public void Test_getInstance() {
        MockInternetGatewayController instance = MockInternetGatewayController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeInternetGateways returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeInternetGateway() throws Exception {
        
        MockInternetGatewayController controller = Mockito.spy(MockInternetGatewayController.class);

        Map<String, MockInternetGateway> allMockInternetGateway = new ConcurrentHashMap<String, MockInternetGateway>();
        MockInternetGateway mockInternetGateway = new MockInternetGateway();
        allMockInternetGateway.put("i-2323", mockInternetGateway);
        MockInternetGateway mockInternetGateway1 = new MockInternetGateway();
        allMockInternetGateway.put("i-23223233", mockInternetGateway1);

        MemberModifier.field(MockInternetGatewayController.class, "allMockInternetGateways").set(controller,
                allMockInternetGateway);

        Collection<MockInternetGateway> collectionOfMockInternetGateway = controller.describeInternetGateways();

        int collectionCount = collectionOfMockInternetGateway.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createInternetGateway() throws Exception {
        
       MockInternetGateway mockInternetGateway = MockInternetGatewayController
                .getInstance()
                .createInternetGateway();

    
        Assert.assertNotNull("Internet gateway created.", mockInternetGateway.getInternetGatewayId());
    }
    
    @Test
    public void Test_deleteInternetGateway() throws Exception {
        
       MockInternetGateway mockInternetGateway = MockInternetGatewayController
                .getInstance()
                .createInternetGateway();
       MockInternetGateway mockInternetGatewayDelete = MockInternetGatewayController
               .getInstance()
               .deleteInternetGateway(mockInternetGateway.getInternetGatewayId());
    
        Assert.assertNotNull("Internet gateway deleted.", mockInternetGatewayDelete.getInternetGatewayId());
    }

}