import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.amazonaws.services.ec2.model.Instance;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;

public class TestExamples {

    @Test
    public void test() {

        // run 10 instances
        String imageId = "ami-12345678"; // pre-defined ami
        int runCount = 10;
        List<Instance> exampleInstances = RunInstancesExample.runInstances(imageId, runCount);

        // describe all instances
        List<Instance> allInstances = DescribeInstancesExample.describeAllInstances();

        // check if all of the 10 new instances are there
        Assert.assertTrue("Actual started instance count (" + exampleInstances.size() + ") does not equal to runCount("
                + runCount + ").",
                exampleInstances.size() == runCount);

        // get max boot time for each instance (in seconds), from properties
        int maxBootSeconds = Integer.parseInt(PropertiesUtils.getProperty("instance.max.boot.time.seconds"));

        // sleep for at least such a time of max boot seconds
        try {
            Thread.sleep((maxBootSeconds + 1) * 1000L);
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
                    InstanceState.RUNNING.equals(inst.getState().getName()));
        }

        // pick one of the example instances, for testing: stop, start and terminate

    }

}
