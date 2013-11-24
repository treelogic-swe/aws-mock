/**
 * File name: Ec2EndpointTest.java Author: Willard Wang Create date: Aug 15,
 * 2013
 */
package com.tlswe.awsmock.ec2;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;

/**
 * This test class covers some sequential test cases to mock ec2 instances in aws-mock (run->stop->start-terminate,
 * etc), along with a case that tests running hundreds of instances.
 *
 * @author Willard Wang
 * @author xma
 */
public class Ec2EndpointTest extends BaseTest {

    /**
     * 2 minutes timeout.
     */
    private static final int TIMEOUT_LEVEL1 = 120000;

    /**
     * 4 minutes timeout.
     */
    private static final int TIMEOUT_LEVEL2 = 240000;

    /**
     * 10 seconds in millisecond.
     */
    private static final int TEN_SECONDS = 10000;

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(Ec2EndpointTest.class);


    /**
     * Test one instance by run->stop.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void sequenceRunStopTest() {
        log.info("Start simple run -> stop test");

        // run
        List<Instance> instances = runInstances(
                AbstractMockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        instances = describeInstances(instances);
        Assert.assertTrue("fail to describe instances", instances.size() == 1);
        Assert.assertEquals("pending", instances.get(0).getState().getName());

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.RUNNING);

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);
        Assert.assertEquals("stopping", stateChanges.get(0).getCurrentState()
                .getName());

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.STOPPED);
    }


    /**
     * Test one instance by run->stop->start->terminate.
     */
    @Test(timeout = TIMEOUT_LEVEL2)
    public final void sequenceRunStopStartTerminateTest() {
        log.info("Start simple run->stop->start->terminate test");
        // run
        List<Instance> instances = runInstances(
                AbstractMockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.RUNNING);

        // stop
        List<InstanceStateChange> stateChanges = stopInstances(instances);
        Assert.assertTrue("fail to stop instances", stateChanges.size() == 1);

        // wait for stopped
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.STOPPED);

        // re-start
        stateChanges = startInstances(instances);
        Assert.assertTrue("fail to re-start instances",
                stateChanges.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.RUNNING);

        // terminate
        stateChanges = terminateInstances(instances);
        Assert.assertTrue("fail to terminate instances",
                stateChanges.size() == 1);

        // wait for terminated
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.TERMINATED);
    }


    /**
     * Test one instance by run->terminate->start. A terminated instance can not start.
     */
    @Test(timeout = TIMEOUT_LEVEL2)
    public final void sequenceRunTerminateStartTest() {
        log.info("Start simple run->terminate->start test");
        // run
        List<Instance> instances = runInstances(
                AbstractMockEc2Instance.InstanceType.M1_SMALL, 1, 1);
        Assert.assertTrue("fail to start instances", instances.size() == 1);

        // wait for running
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.RUNNING);

        // terminate
        List<InstanceStateChange> stateChanges = terminateInstances(instances);
        Assert.assertTrue("fail to terminate instances",
                stateChanges.size() == 1);

        // wait for terminated
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.TERMINATED);

        // start, instance's state should remain terminated.
        stateChanges = startInstances(instances);
        Assert.assertTrue("fail to call start instances",
                stateChanges.size() == 1);

        // wait 10 seconds
        waitForState(instances.get(0).getInstanceId(),
                AbstractMockEc2Instance.InstanceState.RUNNING, TEN_SECONDS);

        instances = describeInstances(instances);
        Assert.assertTrue("number of instances should be 1",
                instances.size() == 1);
        Assert.assertTrue(
                "instance's state should be terminated",
                instances
                        .get(0)
                        .getState()
                        .getName()
                        .equals(AbstractMockEc2Instance.InstanceState.TERMINATED
                                .getName()));
    }


    /**
     * Test starting thousands of instances.
     */
    @Test(timeout = TIMEOUT_LEVEL2)
    public final void hundredsStartTest() {
        log.info("Start hundreds of instances test");

        final int startCount = 100;
        final int maxRandomCount = 200;
        // random 100 to 300 instances
        int count = startCount + new Random().nextInt(maxRandomCount);

        // run
        List<Instance> instances = runInstances(
                AbstractMockEc2Instance.InstanceType.M1_SMALL, count, count);
        Assert.assertTrue("fail to start instances", instances.size() == count);

        // wait for running
        for (Instance i : instances) {
            waitForState(i.getInstanceId(),
                    AbstractMockEc2Instance.InstanceState.RUNNING);
        }
    }
}
