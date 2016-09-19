package com.tlswe.awsmock.ec2.control;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;
import com.tlswe.example.CustomMockEc2Instance;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MockEc2Controller.class})
public class MockEc2ControllerTest {


    @Test
    public void Test_getInstance(){
        MockEc2Controller instance = MockEc2Controller.getInstance();
        Assert.assertNotNull(instance);
    }

    @Test
    public void Test_getMockEc2InstanceUnknown(){
        Object obj = MockEc2Controller.getInstance().getMockEc2Instance("ec2_invalid");
        Assert.assertNull(obj);
    }

    @Test
    public void Test_describeInstancesUnknown(){

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2_1_invalid");
        instanceIDs.add("ec2_2_invalid");

        Collection<AbstractMockEc2Instance> collectionOfAbstractInstances = MockEc2Controller.getInstance().describeInstances(instanceIDs);

        int collectionCount = collectionOfAbstractInstances.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);

        // Should return Null for each invalid EC2
        for(AbstractMockEc2Instance instance : collectionOfAbstractInstances){
            Assert.assertNull(instance);
        }
    }

    @Test
    public void Test_startInstances() throws Exception{

       CustomMockEc2Instance ec2Mocked1 = new CustomMockEc2Instance();
       ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

       CustomMockEc2Instance ec2Mocked2 = new CustomMockEc2Instance();
       ec2Mocked1.setInstanceType(InstanceType.C3_8XLARGE);

       MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
       PowerMockito.when(controller,"getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
       PowerMockito.when(controller,"getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

       Set<String> instanceIDs = new HashSet<String>();
       instanceIDs.add("ec2Mocked1");
       instanceIDs.add("ec2Mocked2");

       controller.startInstances(instanceIDs);

       Assert.assertTrue(ec2Mocked1.isBooting());
       Assert.assertTrue(ec2Mocked2.isBooting());

    }

}
