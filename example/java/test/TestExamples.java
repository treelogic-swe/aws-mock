import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;

/**
 * A test class that covers all the simple and full client-side Java examples.
 *
 * @author xma
 *
 */
public class TestExamples {

    /**
     * One second in millseconds - 1000.
     */
    public static final long ONE_SECOND_IN_MILLSECONDS = 1000L;

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(TestExamples.class);


    /**
     * Tests that describeImages and run,describe,stop,start and terminate instances.
     */
    @Test
    public final void testAllExamples() {

        log.info("Start testing Java examples...");

        // describe all AMIs
        List<Image> allImages = DescribeImagesExample.describeAllImages();
        if (null == allImages || allImages.size() == 0) {
            Assert.fail("No AMI defined in aws-mock!");
        }

        // pick the first AMI and run 10 instances based on it
        String imageId = allImages.get(0).getImageId();
        final int runCount = 10;
        List<Instance> exampleInstances = RunInstancesExample.runInstances(imageId, runCount);

        // check if all of the 10 new instances are there
        Assert.assertTrue("Actual started instance count (" + exampleInstances.size() + ") does not equal to runCount("
                + runCount + ").",
                exampleInstances.size() == runCount);

        // get max boot time for each instance (in seconds), from properties
        int maxBootSeconds = Integer.parseInt(PropertiesUtils.getProperty("instance.max.boot.time.seconds"));

        // sleep for at least such a time of max boot seconds
        try {
            Thread.sleep((maxBootSeconds + 1) * ONE_SECOND_IN_MILLSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        // then check if the 10 new instances are all turned into 'running'
        List<String> exampleInstanceIDs = new ArrayList<String>();
        for (Instance inst : exampleInstances) {
            exampleInstanceIDs.add(inst.getInstanceId());
        }

        // now we describe our 10 instances again, and assert all of them should be running now
        exampleInstances = DescribeInstancesExample.describeInstances(exampleInstanceIDs);
        for (Instance inst : exampleInstances) {
            Assert.assertTrue("Instance state did not turn to 'running' after " + maxBootSeconds + " seconds.",
                    InstanceState.RUNNING.getName().equals(inst.getState().getName()));
        }

        // pick one of the example instances, for testing stop, start and terminate
        String exampleInstanceID = exampleInstanceIDs.get(0);

        // stop the instance
        List<InstanceStateChange> instanceStateChanges = StopInstancesExample.stopInstances(Arrays
                .asList(exampleInstanceID));

        Assert.assertTrue(
                "Wrong state change count found on stopping! expected 1, found " + instanceStateChanges.size(),
                1 == instanceStateChanges.size());

        Assert.assertTrue(
                "Wrong previous state before change! expected 'running', found "
                        + instanceStateChanges.get(0).getPreviousState().getName(), instanceStateChanges.get(0)
                        .getPreviousState().getName().equals(InstanceState.RUNNING.getName()));

        Assert.assertTrue(
                "Wrong current state before change! expected 'stopping', found "
                        + instanceStateChanges.get(0).getCurrentState().getName(), instanceStateChanges.get(0)
                        .getCurrentState().getName().equals(InstanceState.STOPPING.getName()));

        // get max shutdown time for each instance (in seconds), from properties
        int maxShutdownSeconds = Integer.parseInt(PropertiesUtils.getProperty("instance.max.shutdown.time.seconds"));

        // sleep for at least such a time of max shutdown seconds
        try {
            Thread.sleep((maxShutdownSeconds + 1) * ONE_SECOND_IN_MILLSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        // describe that instance again
        exampleInstances = DescribeInstancesExample.describeInstances(Arrays.asList(exampleInstanceID));

        // check if it is stopped now
        Assert.assertTrue("Instance state should be stopped now, but found "
                + exampleInstances.get(0).getState().getName(),
                InstanceState.STOPPED.getName().equals(exampleInstances.get(0).getState().getName()));

        // terminate all the 10 example instances
        instanceStateChanges = TerminateInstancesExample.terminateInstances(exampleInstanceIDs);

        Assert.assertTrue(
                "Wrong state change count found on stopping! expected 10, found " + instanceStateChanges.size(),
                runCount == instanceStateChanges.size());

        // check if all 10 instances' states are changed to 'terminated'
        for (InstanceStateChange stateChange : instanceStateChanges) {
            Assert.assertTrue("Wrong current state after terminataion, expected terminated, found "
                    + stateChange.getCurrentState().getName() + " - instanceID: " + stateChange.getInstanceId(),
                    InstanceState.TERMINATED.getName().equals(stateChange.getCurrentState().getName()));
        }

        // describe those 10 instance again
        exampleInstances = DescribeInstancesExample.describeInstances(exampleInstanceIDs);

        // check again if all of them are terminated
        for (Instance inst : exampleInstances) {
            Assert.assertTrue("Wrong state described after terminataion, expected terminated, found "
                    + inst.getState().getName() + " - instanceID: " + inst.getInstanceId(),
                    InstanceState.TERMINATED.getName().equals(inst.getState().getName()));
        }

        log.info("Tests for Java examples passed.");

    }
}
