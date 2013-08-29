/**
 * File name: Ec2EndpointTest.java Author: Willard Wang Create date: Aug 15,
 * 2013
 */
package test.com.tlswe.awsmock;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;

/**
 * @author Willard Wang
 *
 */
public class Ec2EndpointTest extends BaseTest {

    /**
     * TODO .
     */
    private static final long TIMEOUT_SEQUENCERUNSTOPTEST = 120000;

    /**
     * TODO .
     */
    private static final long TIMEOUT_SEQUENCERUNSTOPSTARTTERMINATETEST = 120000;

    /**
     * TODO .
     */
    @Test(timeout = TIMEOUT_SEQUENCERUNSTOPTEST)
    public final void sequenceRunStopTest() {

        // run
        List<Instance> instances = runInstances("m1.small", 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        instances = describeInstances(instances);
        Assert.assertTrue("fail to describe instances", instances.size() == 1);
        Assert.assertEquals("pending", instances.get(0).getState().getName());

        // wait for running
        waitForState(instances.get(0).getInstanceId(), "running");

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);
        Assert.assertEquals("stopping", stateChanges.get(0).getCurrentState().getName());

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(), "stopped");
    }

    /**
     * TODO .
     */
    @Test(timeout = TIMEOUT_SEQUENCERUNSTOPSTARTTERMINATETEST)
    public final void sequenceRunStopStartTerminateTest() {

        // run
        List<Instance> instances = runInstances("m1.small", 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(), "running");

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(), "stopped");

        // re-start
        stateChanges = startInstances(instances);
        Assert.assertTrue("fail to re-start instances", stateChanges.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(), "running");

        // terminate
        stateChanges = terminateInstances(instances);
        Assert.assertTrue("fail to terminate instances", stateChanges.size() == 1);

        // wait for terminated
        waitForState(instances.get(0).getInstanceId(), "terminated");
    }
}
