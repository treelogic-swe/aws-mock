/**
 * File name: Ec2EndpointTest.java Author: Willard Wang Create date: Aug 15,
 * 2013
 */
package com.tlswe.awsmock.ec2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.tlswe.awsmock.ec2.model.MockEc2Instance;

/**
 * @author Willard Wang
 * 
 */
public class Ec2EndpointTest extends BaseTest {
    /**
     * Log writer for this class.
     */
    private static Logger _log = LoggerFactory.getLogger(Ec2EndpointTest.class);

    /**
     * Test one instance by run->stop.
     */
    @Test(timeout = 120000)
    public void sequenceRunStopTest() {
        _log.info("Start simple run -> stop test");

        // run
        List<Instance> instances = runInstances(
                MockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        instances = describeInstances(instances);
        Assert.assertTrue("fail to describe instances", instances.size() == 1);
        Assert.assertEquals("pending", instances.get(0).getState().getName());

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.RUNNING);

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);
        Assert.assertEquals("stopping", stateChanges.get(0).getCurrentState()
                .getName());

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.STOPPED);
    }

    /**
     * Test one instance by run->stop->start->terminate.
     */
    @Test(timeout = 240000)
    public void sequenceRunStopStartTerminateTest() {
        _log.info("Start simple run->stop->start->terminate test");
        // run
        List<Instance> instances = runInstances(
                MockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.RUNNING);

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.STOPPED);

        // re-start
        stateChanges = startInstances(instances);
        Assert.assertTrue("fail to re-start instances",
                stateChanges.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.RUNNING);

        // terminate
        stateChanges = terminateInstances(instances);
        Assert.assertTrue("fail to terminate instances",
                stateChanges.size() == 1);

        // wait for terminated
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.TERMINATED);
    }

    /**
     * Test one instance by run->terminate->start. A terminated instance can not
     * start.
     */
    @Test(timeout = 240000)
    public void sequenceRunTerminateStartTest() {
        _log.info("Start simple run->terminate->start test");
        // run
        List<Instance> instances = runInstances(
                MockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.RUNNING);

        // terminate
        List<InstanceStateChange> stateChanges = terminateInstances(instances);
        Assert.assertTrue("fail to terminate instances",
                stateChanges.size() == 1);

        // wait for terminated
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.TERMINATED);

        // start, instance's state should remain terminated.
        stateChanges = startInstances(instances);
        Assert.assertTrue("fail to call start instances",
                stateChanges.size() == 1);

        // wait 10 seconds
        waitForState(instances.get(0).getInstanceId(),
                MockEc2Instance.InstanceState.RUNNING, 10000);

        instances = describeInstances(instances);
        Assert.assertTrue("number of instances should be 1",
                instances.size() == 1);
        Assert.assertTrue(
                "instance's state should be terminated",
                instances
                        .get(0)
                        .getState()
                        .getName()
                        .equals(MockEc2Instance.InstanceState.TERMINATED
                                .getName()));
    }

    @Test(timeout = 240000)
    public void thousandsStartTest() {
        _log.info("Start thousands of instances test");

        // random 1000 to 9999 instances
        int count = 1000 + _random.nextInt(9000);

        // run
        List<Instance> instances = runInstances(
                MockEc2Instance.InstanceType.M1_SMALL, count, count);
        Assert.assertTrue("fail to start instances", instances.size() == count);

        // wait for running
        for (Instance i : instances) {
            waitForState(i.getInstanceId(),
                    MockEc2Instance.InstanceState.RUNNING);
        }
    }
}
