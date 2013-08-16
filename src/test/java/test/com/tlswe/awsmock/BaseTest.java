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
    private static String PROPERTY_ACCESS_KEY = "aws.accessKey";
    private static String PROPERTY_SECRET_KEY = "aws.secretKey";
    private static String PROPERTY_ENDPOINT = "ec2.endpoint";
    private static String PROPERTY_MOCK_AMI = "predefined.mock.ami.";

    protected static AmazonEC2Client _amazonEC2Client;
    protected static Properties _testProperties;
    protected static List<String> _predefinedAMIs;

    private static synchronized void initTestProperties() {
        if (_testProperties == null) {
            _testProperties = new Properties();
            try {
                _testProperties.load(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("aws-mock.test.properties"));
            } catch (IOException e) {
                Assert.fail("fail to load 'aws-mock.test.properties' - "
                        + e.getMessage());
            }
        }
    }

    /**
     * 
     */
    private static synchronized void initPredefinedAMIs() {
        if (_predefinedAMIs == null) {
            _predefinedAMIs = new ArrayList<String>();
            for (int i = 1; _testProperties.containsKey(PROPERTY_MOCK_AMI + i); i++) {
                _predefinedAMIs.add(_testProperties
                        .getProperty(PROPERTY_MOCK_AMI + i));
            }
        }
    }

    private static synchronized void initEc2Client() {
        if (_amazonEC2Client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(
                    _testProperties.getProperty(PROPERTY_ACCESS_KEY),
                    _testProperties.getProperty(PROPERTY_SECRET_KEY));
            _amazonEC2Client = new AmazonEC2Client(credentials);
            _amazonEC2Client.setEndpoint(_testProperties
                    .getProperty(PROPERTY_ENDPOINT));
        }
    }

    protected static String randomAMI() {
        Assert.assertNotNull(_predefinedAMIs);
        Assert.assertTrue("There's no predefined AMI id.",
                _predefinedAMIs.size() > 0);

        Random random = new Random();
        int index = random.nextInt(_predefinedAMIs.size());
        return _predefinedAMIs.get(index);
    }

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
     * @return
     */
    protected Collection<String> getInstanceIds(List<Instance> instances) {
        List<String> ids = new ArrayList<String>();
        for (Instance i : instances)
            ids.add(i.getInstanceId());
        return ids;
    }

    protected List<Instance> runInstances(String instanceType, int minCount,
            int maxCount) {
        RunInstancesRequest request = new RunInstancesRequest();

        String imageId = randomAMI();

        request.withImageId(imageId).withInstanceType(instanceType)
                .withMinCount(minCount).withMaxCount(maxCount);

        RunInstancesResult result = _amazonEC2Client.runInstances(request);
        return result.getReservation().getInstances();
    }

    protected List<InstanceStateChange> startInstances(
            Collection<String> instanceIds) {

        StartInstancesRequest request = new StartInstancesRequest();
        request.setInstanceIds(instanceIds);
        StartInstancesResult result = _amazonEC2Client.startInstances(request);
        return result.getStartingInstances();
    }

    protected List<InstanceStateChange> startInstances(List<Instance> instances) {
        return startInstances(getInstanceIds(instances));
    }

    protected List<InstanceStateChange> stopInstances(Collection<String> ids) {
        StopInstancesRequest request = new StopInstancesRequest();
        request.setInstanceIds(ids);
        StopInstancesResult result = _amazonEC2Client.stopInstances(request);
        return result.getStoppingInstances();
    }

    protected List<InstanceStateChange> stopInstances(List<Instance> instances) {
        return stopInstances(getInstanceIds(instances));
    }

    protected List<InstanceStateChange> terminateInstances(
            Collection<String> ids) {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.setInstanceIds(ids);
        TerminateInstancesResult result = _amazonEC2Client
                .terminateInstances(request);
        return result.getTerminatingInstances();
    }

    protected List<InstanceStateChange> terminateInstances(
            List<Instance> instances) {
        return terminateInstances(getInstanceIds(instances));
    }

    protected List<Instance> describeInstances(Collection<String> instanceIds) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        DescribeInstancesResult result = _amazonEC2Client
                .describeInstances(request);
        Assert.assertTrue(result.getReservations().size() > 0);
        return result.getReservations().get(0).getInstances();
    }

    protected List<Instance> describeInstances(List<Instance> instances) {

        return describeInstances(getInstanceIds(instances));
    }

    protected void waitForState(String instanceId, String state) {
        while (true) {
            List<Instance> instances = describeInstances(Arrays
                    .asList(instanceId));
            if (instances.get(0).getState().getName().equals(state))
                return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
