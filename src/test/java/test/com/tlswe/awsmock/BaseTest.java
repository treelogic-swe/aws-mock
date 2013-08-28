/**
 * File name: BaseTest.java Author: Willard Wang Create date: Aug 8, 2013
 */
package test.com.tlswe.awsmock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import junit.framework.Assert;

import org.junit.BeforeClass;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/**
 * @author Willard Wang
 *
 */
public class BaseTest {

    /**
     * TODO .
     */
    private static final long POLLING_INTERVAL_MILLSECS = 1000L;

    /**
     * TODO .
     */
    private static final String PROPERTY_ACCESS_KEY = "aws.accessKey";

    /**
     * TODO .
     */
    private static final String PROPERTY_SECRET_KEY = "aws.secretKey";

    /**
     * TODO .
     */
    private static final String PROPERTY_ENDPOINT = "ec2.endpoint";

    /**
     * TODO .
     */
    private static final String PROPERTY_MOCK_AMI = "predefined.mock.ami.";

    /**
     * TODO .
     */
    private static AmazonEC2Client amazonEC2Client;

    /**
     * TODO .
     */
    private static Properties testProperties;

    /**
     * TODO .
     */
    private static List<String> predefinedAMIs;

    /**
     * TODO .
     */
    private static synchronized void initTestProperties() {
        if (testProperties == null) {
            testProperties = new Properties();
            try {
                testProperties.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("aws-mock.test.properties"));
            } catch (IOException e) {
                Assert.fail("fail to load 'aws-mock.test.properties' - " + e.getMessage());
            }
        }
    }

    /**
     * TODO .
     */
    private static synchronized void initPredefinedAMIs() {
        if (predefinedAMIs == null) {
            predefinedAMIs = new ArrayList<String>();
            for (int i = 1; testProperties.containsKey(PROPERTY_MOCK_AMI + i); i++) {
                predefinedAMIs.add(testProperties.getProperty(PROPERTY_MOCK_AMI + i));
            }
        }
    }

    /**
     * TODO .
     */
    private static synchronized void initEc2Client() {
        if (amazonEC2Client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(testProperties.getProperty(PROPERTY_ACCESS_KEY),
                    testProperties.getProperty(PROPERTY_SECRET_KEY));
            amazonEC2Client = new AmazonEC2Client(credentials);
            amazonEC2Client.setEndpoint(testProperties.getProperty(PROPERTY_ENDPOINT));
        }
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    protected static String randomAMI() {
        Assert.assertNotNull(predefinedAMIs);
        Assert.assertTrue("There's no predefined AMI id.", predefinedAMIs.size() > 0);

        Random random = new Random();
        int index = random.nextInt(predefinedAMIs.size());
        return predefinedAMIs.get(index);
    }

    /**
     * TODO .
     */
    @BeforeClass
    public static void setup() {
        initTestProperties();
        initPredefinedAMIs();
        initEc2Client();
    }

    /**
     * Construct a List of Instances' IDs.
     *
     * @param instances
     *            TODO
     * @return TODO
     */
    protected final Collection<String> getInstanceIds(final List<Instance> instances) {
        List<String> ids = new ArrayList<String>();
        for (Instance i : instances) {
            ids.add(i.getInstanceId());
        }
        return ids;
    }

    /**
     *
     * @param instanceType
     *            TODO
     * @param minCount
     *            TODO
     * @param maxCount
     *            TODO
     * @return TODO
     */
    protected final List<Instance> runInstances(final String instanceType, final int minCount, final int maxCount) {
        RunInstancesRequest request = new RunInstancesRequest();

        String imageId = randomAMI();

        request.withImageId(imageId).withInstanceType(instanceType).withMinCount(minCount).withMaxCount(maxCount);

        RunInstancesResult result = amazonEC2Client.runInstances(request);
        return result.getReservation().getInstances();
    }

    /**
     *
     * @param instanceIds
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> startInstances(final Collection<String> instanceIds) {

        StartInstancesRequest request = new StartInstancesRequest();
        request.setInstanceIds(instanceIds);
        StartInstancesResult result = amazonEC2Client.startInstances(request);
        return result.getStartingInstances();
    }

    /**
     *
     * @param instances
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> startInstances(final List<Instance> instances) {
        return startInstances(getInstanceIds(instances));
    }

    /**
     *
     * @param ids
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> stopInstances(final Collection<String> ids) {
        StopInstancesRequest request = new StopInstancesRequest();
        request.setInstanceIds(ids);
        StopInstancesResult result = amazonEC2Client.stopInstances(request);
        return result.getStoppingInstances();
    }

    /**
     *
     * @param instances
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> stopInstances(final List<Instance> instances) {
        return stopInstances(getInstanceIds(instances));
    }

    /**
     *
     * @param ids
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> terminateInstances(final Collection<String> ids) {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.setInstanceIds(ids);
        TerminateInstancesResult result = amazonEC2Client.terminateInstances(request);
        return result.getTerminatingInstances();
    }

    /**
     *
     * @param instances
     *            TODO
     * @return TODO
     */
    protected final List<InstanceStateChange> terminateInstances(final List<Instance> instances) {
        return terminateInstances(getInstanceIds(instances));
    }

    /**
     *
     * @param instanceIds
     *            TODO
     * @return TODO
     */
    protected final List<Instance> describeInstances(final Collection<String> instanceIds) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        DescribeInstancesResult result = amazonEC2Client.describeInstances(request);
        Assert.assertTrue(result.getReservations().size() > 0);
        return result.getReservations().get(0).getInstances();
    }

    /**
     *
     * @param instances
     *            TODO
     * @return TODO
     */
    protected final List<Instance> describeInstances(final List<Instance> instances) {

        return describeInstances(getInstanceIds(instances));
    }

    /**
     *
     * @param instanceId
     *            TODO
     * @param state
     *            TODO
     */
    protected final void waitForState(final String instanceId, final String state) {
        while (true) {
            List<Instance> instances = describeInstances(Arrays.asList(instanceId));
            if (instances.get(0).getState().getName().equals(state)) {
                return;
            }
            try {
                Thread.sleep(POLLING_INTERVAL_MILLSECS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
