package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;
import com.tlswe.awsmock.ec2.model.DefaultMockEc2Instance;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEc2Controller.class, DefaultMockEc2Instance.class })
public class MockEc2ControllerTest {

    @Test
    public void Test_getInstance() {
        MockEc2Controller instance = MockEc2Controller.getInstance();
        Assert.assertNotNull(instance);
    }


    @Test
    public void Test_getMockEc2InstanceUnknown() {
        Object obj = MockEc2Controller.getInstance().getMockEc2Instance("ec2_invalid");
        Assert.assertNull(obj);
    }


    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeInstances returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeInstancesUnknown() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put(ec2Mocked1.getInstanceID(), ec2Mocked1);
        allMockEc2Instances.put(ec2Mocked2.getInstanceID(), ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2_1_invalid");
        instanceIDs.add("ec2_2_invalid");

        Collection<AbstractMockEc2Instance> collectionOfAbstractInstances = MockEc2Controller.getInstance()
                .describeInstances(instanceIDs);

        int collectionCount = collectionOfAbstractInstances.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);

        // Should return Null for each invalid EC2
        for (AbstractMockEc2Instance instance : collectionOfAbstractInstances) {
            Assert.assertNull(instance);
        }
    }


    @Test
    public void Test_describeInstancesNoIds() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put(ec2Mocked1.getInstanceID(), ec2Mocked1);
        allMockEc2Instances.put(ec2Mocked2.getInstanceID(), ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);

        // Should return the whole collection of instances
        Assert.assertTrue(controller.describeInstances(null).size() == allMockEc2Instances.size());
        Assert.assertTrue(controller.describeInstances(new HashSet<String>()).size() == allMockEc2Instances.size());

        for (AbstractMockEc2Instance ec2MockedInstance : controller.describeInstances(null)) {
            Assert.assertTrue(allMockEc2Instances.containsKey(ec2MockedInstance.getInstanceID()));
        }

        for (AbstractMockEc2Instance ec2MockedInstance : controller.describeInstances(new HashSet<String>())) {
            Assert.assertTrue(allMockEc2Instances.containsKey(ec2MockedInstance.getInstanceID()));
        }
    }


    @Test
    public void Test_describeInstances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add(ec2Mocked1.getInstanceID()); // we will search for only the first instance

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put(ec2Mocked1.getInstanceID(), ec2Mocked1);
        allMockEc2Instances.put(ec2Mocked2.getInstanceID(), ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);

        Collection<AbstractMockEc2Instance> collectionOfMockedInstances = controller.describeInstances(instanceIDs);
        Assert.assertTrue(collectionOfMockedInstances.size() == 1);

        for (AbstractMockEc2Instance instance : collectionOfMockedInstances) {
            Assert.assertTrue(instance == ec2Mocked1);
        }

    }


    @Test
    public void Test_startInstances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        controller.startInstances(instanceIDs);

        Assert.assertTrue(ec2Mocked1.isBooting());
        Assert.assertTrue(ec2Mocked2.isBooting());

    }


    @Test
    public void Test_stopInstances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        controller.startInstances(instanceIDs); // first we need to start to bring to booting or starting state
        controller.stopInstances(instanceIDs);

        Assert.assertTrue(!ec2Mocked1.isBooting() && ec2Mocked1.isStopping());
        Assert.assertTrue(!ec2Mocked2.isBooting() && ec2Mocked2.isStopping());

    }


    @Test
    public void Test_terminateInstances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        controller.startInstances(instanceIDs); // first we need to start to bring to booting or starting state
        controller.terminateInstances(instanceIDs);

        Assert.assertTrue(ec2Mocked1.isTerminated());
        Assert.assertTrue(ec2Mocked2.isTerminated());

    }


    @Test
    public void Test_getAllMockEc2Instances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put("ec2Mocked1", ec2Mocked1);
        allMockEc2Instances.put("ec2Mocked2", ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);
        Collection<AbstractMockEc2Instance> collectionOfEc2Instances = controller.getAllMockEc2Instances();

        Assert.assertTrue(collectionOfEc2Instances.size() == 2);
        Assert.assertTrue(collectionOfEc2Instances.contains(ec2Mocked1));
        Assert.assertTrue(collectionOfEc2Instances.contains(ec2Mocked2));

    }


    @Test
    public void Test_getMockEc2Instance() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put("ec2Mocked1", ec2Mocked1);
        allMockEc2Instances.put("ec2Mocked2", ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);
        Assert.assertTrue(controller.getMockEc2Instance("ec2Mocked1") == ec2Mocked1);
        Assert.assertTrue(controller.getMockEc2Instance("ec2Mocked2") == ec2Mocked2);

    }


    @Test
    public void Test_restoreAllMockEc2Instances() {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        DefaultMockEc2Instance ec2Mocked1 = Mockito.spy(DefaultMockEc2Instance.class);
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = Mockito.spy(DefaultMockEc2Instance.class);
        ec2Mocked1.setInstanceType(InstanceType.C3_8XLARGE);

        List<AbstractMockEc2Instance> collectionOfMockEc2Instances = new ArrayList<AbstractMockEc2Instance>();
        collectionOfMockEc2Instances.add(ec2Mocked1);
        collectionOfMockEc2Instances.add(ec2Mocked2);

        controller.restoreAllMockEc2Instances(collectionOfMockEc2Instances);

        Collection<AbstractMockEc2Instance> returnedInstances = controller.getAllMockEc2Instances();
        Assert.assertTrue(returnedInstances.size() == 2);
        Assert.assertTrue(returnedInstances.contains(ec2Mocked1));
        Assert.assertTrue(returnedInstances.contains(ec2Mocked2));

    }


    @Test
    public void Test_cleanupTerminatedInstances() throws Exception {

        DefaultMockEc2Instance ec2Mocked1 = new DefaultMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        DefaultMockEc2Instance ec2Mocked2 = new DefaultMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put(ec2Mocked1.getInstanceID(), ec2Mocked1);
        allMockEc2Instances.put(ec2Mocked2.getInstanceID(), ec2Mocked2);

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add(ec2Mocked1.getInstanceID());
        instanceIDs.add(ec2Mocked2.getInstanceID());

        controller.startInstances(instanceIDs); // first we need to start to bring to booting or starting state
        controller.terminateInstances(instanceIDs); // instances should now be in terminated state

        controller.cleanupTerminatedInstances(1);
        Thread.sleep(1000); // delay needed to ensure thread gets executed
        controller.destroyCleanupTerminatedInstanceTimer(); // need to stop the timer
        Assert.assertTrue(controller.getAllMockEc2Instances().size() == 0);

    }


    @Test(expected = BadEc2RequestException.class)
    public void Test_runInstancesBadRequestInstanceType() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        controller.runInstances(DefaultMockEc2Instance.class, "ImageName", "InvalidName", 10, 1);
    }


    @Test(expected = BadEc2RequestException.class)
    public void Test_runInstancesBadRequestMaxCountHigh() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        controller.runInstances(DefaultMockEc2Instance.class, "ImageName", InstanceType.C1_MEDIUM.getName(), 1, 10001);
    }


    @Test(expected = BadEc2RequestException.class)
    public void Test_runInstancesBadRequestMinCountLow() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        controller.runInstances(DefaultMockEc2Instance.class, "ImageName", InstanceType.C1_MEDIUM.getName(), 0, 10);
    }


    @Test(expected = BadEc2RequestException.class)
    public void Test_runInstancesMinCountGreaterThanMaxCount() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        controller.runInstances(DefaultMockEc2Instance.class, "ImageName", InstanceType.C1_MEDIUM.getName(), 11, 10);
    }


    @Test(expected = AwsMockException.class)
    public void Test_runInstancesAwsMockException() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        // shouldn't be able to start abstract class
        controller.runInstances(AbstractMockEc2Instance.class, "ImageName", InstanceType.C1_MEDIUM.getName(), 1, 1);
    }


    @Test
    public void Test_runInstances() throws Exception {

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        controller.runInstances(DefaultMockEc2Instance.class, "ImageName", InstanceType.C1_MEDIUM.getName(), 1, 1);
    }

}
