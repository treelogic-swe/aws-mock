/**
 * File name: BaseTest.java Author: Willard Wang Create date: Aug 8, 2013
 */
package com.tlswe.awsmock.ec2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.tlswe.awsmock.ec2.model.MockEc2Instance.InstanceState;
import com.tlswe.awsmock.ec2.model.MockEc2Instance.InstanceType;

/**
 * @author Willard Wang
 * 
 */
public class BaseTest {
    /**
     * Log writer for this class.
     */
    private static Logger _log = LoggerFactory.getLogger(BaseTest.class);

    /**
     * Property key for AWS access key.
     */
    private static String PROPERTY_ACCESS_KEY = "aws.accessKey";

    /**
     * Property key for AWS secret key.
     */
    private static String PROPERTY_SECRET_KEY = "aws.secretKey";

    /**
     * Property key for endpoint URL.
     */
    private static String PROPERTY_ENDPOINT = "ec2.endpoint";

    /**
     * Property key for predefined AMI IDs. The key for predefined AMI id is a
     * String in format "predefined.mock.ami.id" where id is an integer number.
     */
    private static String PROPERTY_MOCK_AMI = "predefined.mock.ami.";

    /**
     * EC2 client singleton.
     */
    protected static AmazonEC2Client _amazonEC2Client;

    /**
     * Properties load from file 'aws-mock.test.properties'.
     */
    protected static Properties _testProperties;

    /**
     * Predefined AMIs load from {@link #_testProperties}
     */
    protected static List<String> _predefinedAMIs;

    /**
     * Load test properties from file 'aws-mock.test.properties'.
     */
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
     * Load predefined AMIs from test properties. Invoked after
     * {@link #initTestProperties()}.
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

    /**
     * Load ec2 client URL from test properties and create an ec2 client
     * instance. Invoked after {@link #initTestProperties()}.
     */
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

    /**
     * 
     * @return a random predefined AMI id.
     */
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
     * @return collection of IDs
     */
    protected Collection<String> getInstanceIds(List<Instance> instances) {
        List<String> ids = new ArrayList<String>();
        for (Instance i : instances)
            ids.add(i.getInstanceId());
        return ids;
    }

    /**
     * Run instances with a random AMI ID.
     * 
     * @param type
     *            instance type
     * @param minCount
     * @param maxCount
     * @return a list of instances started.
     */
    protected List<Instance> runInstances(InstanceType type, int minCount,
            int maxCount) {
        _log.info("Run instances: type=" + type + ", minCount=" + minCount
                + ", maxCount=" + maxCount);
        RunInstancesRequest request = new RunInstancesRequest();

        String imageId = randomAMI();

        request.withImageId(imageId).withInstanceType(type.getName())
                .withMinCount(minCount).withMaxCount(maxCount);

        RunInstancesResult result = _amazonEC2Client.runInstances(request);
        return result.getReservation().getInstances();
    }

    /**
     * Start instances.
     * 
     * @param instanceIds
     *            instances' IDs
     * @return
     */
    protected List<InstanceStateChange> startInstances(
            Collection<String> instanceIds) {
        _log.info("Start instances:" + toString(instanceIds));

        StartInstancesRequest request = new StartInstancesRequest();
        request.setInstanceIds(instanceIds);
        StartInstancesResult result = _amazonEC2Client.startInstances(request);
        return result.getStartingInstances();
    }

    /**
     * Start instances.
     * 
     * @param instances
     *            list of instances
     * @return
     */
    protected List<InstanceStateChange> startInstances(List<Instance> instances) {
        return startInstances(getInstanceIds(instances));
    }

    /**
     * Stop instances.
     * 
     * @param instanceIds
     *            instances' IDs
     * @return
     */
    protected List<InstanceStateChange> stopInstances(
            Collection<String> instanceIds) {
        _log.info("Stop instances:" + toString(instanceIds));
        StopInstancesRequest request = new StopInstancesRequest();
        request.setInstanceIds(instanceIds);
        StopInstancesResult result = _amazonEC2Client.stopInstances(request);
        return result.getStoppingInstances();
    }

    /**
     * Stop instances.
     * 
     * @param instances
     *            list of instances
     * @return
     */
    protected List<InstanceStateChange> stopInstances(List<Instance> instances) {
        return stopInstances(getInstanceIds(instances));
    }

    /**
     * Terminate instances.
     * 
     * @param instanceIds
     *            instances' IDs
     * @return
     */
    protected List<InstanceStateChange> terminateInstances(
            Collection<String> instanceIds) {
        _log.info("Terminate instances:" + toString(instanceIds));
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.setInstanceIds(instanceIds);
        TerminateInstancesResult result = _amazonEC2Client
                .terminateInstances(request);
        return result.getTerminatingInstances();
    }

    /**
     * Terminate instances.
     * 
     * @param instances
     *            list of instances
     * @return
     */
    protected List<InstanceStateChange> terminateInstances(
            List<Instance> instances) {
        return terminateInstances(getInstanceIds(instances));
    }

    /**
     * 
     * @param instanceIds
     *            instances' IDs
     * @param enableLogging
     * @return
     */
    protected List<Instance> describeInstances(Collection<String> instanceIds,
            boolean enableLogging) {
        if (enableLogging)
            _log.info("Describe instances:" + toString(instanceIds));
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        DescribeInstancesResult result = _amazonEC2Client
                .describeInstances(request);
        Assert.assertTrue(result.getReservations().size() > 0);
        return result.getReservations().get(0).getInstances();
    }

    /**
     * Describe instances.
     * 
     * @param instances
     *            list of instances
     * @return
     */
    protected List<Instance> describeInstances(List<Instance> instances) {

        return describeInstances(getInstanceIds(instances));
    }

    /**
     * Describe instances.
     * 
     * @param instanceIds
     *            instances' IDs
     * @return
     */
    protected List<Instance> describeInstances(Collection<String> instanceIds) {
        return describeInstances(instanceIds, true);
    }

    /**
     * Wait an instance reaching target {@link InstanceState} by describing the
     * instance every second until it reach target state.
     * 
     * @param instanceId
     *            instance's ID
     * @param state
     *            target state
     */
    protected void waitForState(String instanceId, InstanceState state) {
        _log.info("Wait instance " + instanceId + " reaching state " + state);
        while (true) {
            List<Instance> instances = describeInstances(
                    Arrays.asList(instanceId), false);
            if (instances.get(0).getState().getName().equals(state.getName()))
                return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convert a collection of instance IDS into a space-separated String.
     * 
     * @param instanceIds
     * @return
     */
    private String toString(Collection<String> instanceIds) {
        StringBuilder builder = new StringBuilder();
        for (String id : instanceIds) {
            builder.append(" " + id);
        }
        return builder.toString();
    }
}
