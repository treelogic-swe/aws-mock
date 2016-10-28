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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeInternetGatewaysRequest;
import com.amazonaws.services.ec2.model.DescribeInternetGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeRouteTablesRequest;
import com.amazonaws.services.ec2.model.DescribeRouteTablesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;

/**
 * Base underlying class for doing the fundamental calls to aws ec2 interfaces, with neat utility methods which can be
 * made use of by test cases that test aws-mock.
 *
 * @author Willard Wang
 * @author xma
 */
public class BaseTest {

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(BaseTest.class);

    /**
     * One second in millisecond.
     */
    private static final int ONE_SECOND = 1000;

    /**
     * Property key for AWS access key.
     */
    private static final String PROPERTY_ACCESS_KEY = "aws.accessKey";

    /**
     * Property key for AWS secret key.
     */
    private static final String PROPERTY_SECRET_KEY = "aws.secretKey";

    /**
     * Property key for endpoint URL.
     */
    private static final String PROPERTY_ENDPOINT = "ec2.endpoint";

    /**
     * Property key for predefined AMI IDs. The key for predefined AMI id is a String in format "predefined.mock.ami.id"
     * where id is an integer number.
     */
    private static final String PROPERTY_MOCK_AMI = "predefined.mock.ami.";

    /**
     * EC2 client singleton.
     */
    private static AmazonEC2Client amazonEC2Client;

    private static String INTEGRATION_TEST_PROPERTIES_FILE = "aws-mock.integration-test.properties";
    /**
     * Properties load from file {@link INTEGRATION_TEST_PROPERTIES_FILE}.
     */
    private static Properties testProperties;

    /**
     * Predefined AMIs load from {@link #testProperties}.
     */
    private static List<String> predefinedAMIs;


    /**
     * Load test properties from file {@link INTEGRATION_TEST_PROPERTIES_FILE}.
     */
    private static synchronized void initTestProperties() {
        if (testProperties == null) {
            testProperties = new Properties();
            try {
                testProperties.load(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(INTEGRATION_TEST_PROPERTIES_FILE));
            } catch (IOException e) {
                Assert.fail("fail to load '" + INTEGRATION_TEST_PROPERTIES_FILE + "' - "
                        + e.getMessage());
            }
        }
    }


    /**
     * Load predefined AMIs from test properties. Invoked after {@link #initTestProperties()}.
     */
    private static synchronized void initPredefinedAMIs() {
        if (predefinedAMIs == null) {
            predefinedAMIs = new ArrayList<String>();
            for (int i = 1; testProperties.containsKey(PROPERTY_MOCK_AMI + i); i++) {
                predefinedAMIs.add(testProperties.getProperty(PROPERTY_MOCK_AMI
                        + i));
            }
        }
    }


    /**
     * Load ec2 client URL from test properties and create an ec2 client instance. Invoked after
     * {@link #initTestProperties()}.
     */
    private static synchronized void initEc2Client() {
        if (amazonEC2Client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(
                    testProperties.getProperty(PROPERTY_ACCESS_KEY),
                    testProperties.getProperty(PROPERTY_SECRET_KEY));
            amazonEC2Client = new AmazonEC2Client(credentials);
            amazonEC2Client.setEndpoint(testProperties
                    .getProperty(PROPERTY_ENDPOINT));
        }
    }


    /**
     * @return a random predefined AMI id.
     */
    protected static String randomAMI() {
        Assert.assertNotNull(predefinedAMIs);
        Assert.assertTrue("There's no predefined AMI id.",
                predefinedAMIs.size() > 0);

        int index = new Random().nextInt(predefinedAMIs.size());
        return predefinedAMIs.get(index);
    }


    /**
     * Read test properties, setup predefined AMIs and create EC2 client.
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
     *            list of instances
     * @return collection of IDs
     */
    protected final Collection<String> getInstanceIds(
            final List<Instance> instances) {
        List<String> ids = new ArrayList<String>();
        for (Instance i : instances) {
            ids.add(i.getInstanceId());
        }
        return ids;
    }


    /**
     * Run instances with a random AMI ID.
     *
     * @param type
     *            instance type
     * @param minCount
     *            minimum start up instance number
     * @param maxCount
     *            maximum start up instance number
     * @return a list of instances started.
     */
    protected final List<Instance> runInstances(final InstanceType type,
            final int minCount, final int maxCount) {
        log.info("Run instances: type=" + type + ", minCount=" + minCount
                + ", maxCount=" + maxCount);
        RunInstancesRequest request = new RunInstancesRequest();

        String imageId = randomAMI();

        request.withImageId(imageId).withInstanceType(type.getName())
                .withMinCount(minCount).withMaxCount(maxCount);

        RunInstancesResult result = amazonEC2Client.runInstances(request);
        return result.getReservation().getInstances();
    }


    /**
     * Start instances.
     *
     * @param instanceIds
     *            instances' IDs
     * @return list of instances change
     */
    protected final List<InstanceStateChange> startInstances(
            final Collection<String> instanceIds) {
        log.info("Start instances:" + toString(instanceIds));

        StartInstancesRequest request = new StartInstancesRequest();
        request.setInstanceIds(instanceIds);
        StartInstancesResult result = amazonEC2Client.startInstances(request);
        return result.getStartingInstances();
    }


    /**
     * Start instances.
     *
     * @param instances
     *            list of instances
     * @return list of instances change
     */
    protected final List<InstanceStateChange> startInstances(
            final List<Instance> instances) {
        return startInstances(getInstanceIds(instances));
    }


    /**
     * Stop instances.
     *
     * @param instanceIds
     *            instances' IDs
     * @return list of instances change
     */
    protected final List<InstanceStateChange> stopInstances(
            final Collection<String> instanceIds) {
        log.info("Stop instances:" + toString(instanceIds));
        StopInstancesRequest request = new StopInstancesRequest();
        request.setInstanceIds(instanceIds);
        StopInstancesResult result = amazonEC2Client.stopInstances(request);
        return result.getStoppingInstances();
    }


    /**
     * Stop instances.
     *
     * @param instances
     *            list of instances
     * @return list of instances change
     */
    protected final List<InstanceStateChange> stopInstances(
            final List<Instance> instances) {
        return stopInstances(getInstanceIds(instances));
    }


    /**
     * Terminate instances.
     *
     * @param instanceIds
     *            instances' IDs
     * @return list of instances change
     */
    protected final List<InstanceStateChange> terminateInstances(
            final Collection<String> instanceIds) {
        log.info("Terminate instances:" + toString(instanceIds));
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.setInstanceIds(instanceIds);
        TerminateInstancesResult result = amazonEC2Client
                .terminateInstances(request);
        return result.getTerminatingInstances();
    }


    /**
     * Terminate instances.
     *
     * @param instances
     *            list of instances
     * @return list of instances change
     */
    protected final List<InstanceStateChange> terminateInstances(
            final List<Instance> instances) {
        return terminateInstances(getInstanceIds(instances));
    }


    /**
     * @param instanceIds
     *            instances' IDs
     * @param enableLogging
     *            log to standard out
     * @return list of instances
     */
    protected final List<Instance> describeInstances(
            final Collection<String> instanceIds, final boolean enableLogging) {
        if (enableLogging) {
            log.info("Describe instances:" + toString(instanceIds));
        }
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        DescribeInstancesResult result = amazonEC2Client
                .describeInstances(request);
        Assert.assertTrue(result.getReservations().size() > 0);

        List<Instance> instanceList = new ArrayList<Instance>();

        for (Reservation reservation : result.getReservations()) {
            List<Instance> instances = reservation.getInstances();

            if (null != instances) {
                for (Instance i : instances) {
                    instanceList.add(i);
                }
            }
        }

        return instanceList;
    }


    /**
     * Describe instances.
     *
     * @param instances
     *            list of instances
     * @return list of described instances
     */
    protected final List<Instance> describeInstances(
            final List<Instance> instances) {

        return describeInstances(getInstanceIds(instances));
    }


    /**
     * Describe instances.
     *
     * @param instanceIds
     *            instances' IDs
     * @return list of instances
     */
    protected final List<Instance> describeInstances(
            final Collection<String> instanceIds) {
        return describeInstances(instanceIds, true);
    }


    /**
     * Describe non terminated instances.
     *
     * @param instanceIds
     *            instances' IDs
     * @return list of non terminated instances
     */
    protected final List<Instance> describeNonTerminatedInstances(
            final List<Instance> instanceIds) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(getInstanceIds(instanceIds));

        // set the request filter
        Filter nonTerminatedFilter = getNonTerminatedInstancesFilter();
        request.getFilters().add(nonTerminatedFilter);

        DescribeInstancesResult result = amazonEC2Client
                .describeInstances(request);
        Assert.assertTrue(result.getReservations().size() > 0);

        List<Instance> instanceList = new ArrayList<Instance>();

        for (Reservation reservation : result.getReservations()) {
            List<Instance> instances = reservation.getInstances();

            if (null != instances) {
                for (Instance i : instances) {
                    instanceList.add(i);
                }
            }
        }

        return instanceList;
    }


    /**
     * Create a filter to only get non terminated instances.
     *
     * @return list of instances
     */
    protected final Filter getNonTerminatedInstancesFilter() {
        List<String> stateValues = new ArrayList<String>(Arrays.asList(InstanceState.RUNNING.getName(),
                InstanceState.PENDING.getName(), InstanceState.STOPPING.getName(),
                InstanceState.STOPPED.getName(), InstanceState.SHUTTING_DOWN.getName()));

        Filter runningInstanceFilter = new Filter();
        runningInstanceFilter.setValues(stateValues);
        return runningInstanceFilter;
    }


    /**
     * Describe VPCs.
     *
     * @return List of vpcs
     */
    protected final List<Vpc> describeVpcs() {
        DescribeVpcsRequest req = new DescribeVpcsRequest();
        DescribeVpcsResult result = amazonEC2Client.describeVpcs(req);
        List<Vpc> vpcs = result.getVpcs();
        return vpcs;
    }


    /**
     * Describe security group.
     *
     * @return SecurityGroup
     */
    protected final SecurityGroup getSecurityGroup() {
        SecurityGroup cellGroup = null;

        DescribeSecurityGroupsRequest req = new DescribeSecurityGroupsRequest();
        DescribeSecurityGroupsResult result = amazonEC2Client.describeSecurityGroups(req);
        if (result != null && !result.getSecurityGroups().isEmpty()) {
            cellGroup = result.getSecurityGroups().get(0);
        }

        return cellGroup;
    }


    /**
     * Describe internet gateway.
     *
     * @return InternetGateway
     */
    protected final InternetGateway getInternetGateway() {
        InternetGateway internetGateway = null;

        DescribeInternetGatewaysRequest req = new DescribeInternetGatewaysRequest();
        DescribeInternetGatewaysResult result = amazonEC2Client.describeInternetGateways(req);
        if (result != null && !result.getInternetGateways().isEmpty()) {
            internetGateway = result.getInternetGateways().get(0);
        }

        return internetGateway;
    }


    /**
     * Describe route table.
     *
     * @return RouteTable
     */
    protected final RouteTable getRouteTable() {
        RouteTable routeTable = null;

        DescribeRouteTablesRequest req = new DescribeRouteTablesRequest();
        DescribeRouteTablesResult result = amazonEC2Client.describeRouteTables(req);
        if (result != null && !result.getRouteTables().isEmpty()) {
            routeTable = result.getRouteTables().get(0);
        }

        return routeTable;
    }

    /**
     * Describe Volume.
     *
     * @return Volume
     */
    protected final Volume getVolume() {
        Volume volume = null;

        DescribeVolumesRequest req = new DescribeVolumesRequest();
        DescribeVolumesResult result = amazonEC2Client.describeVolumes(req);
        if (result != null && !result.getVolumes().isEmpty()) {
        	volume = result.getVolumes().get(0);
        }

        return volume;
    }

    /**
     * Describe Subnet.
     *
     * @return Subnet
     */
    protected final Subnet getSubnet() {
    	Subnet subnet = null;

        DescribeSubnetsRequest req = new DescribeSubnetsRequest();
        DescribeSubnetsResult result = amazonEC2Client.describeSubnets(req);
        if (result != null && !result.getSubnets().isEmpty()) {
        	subnet = result.getSubnets().get(0);
        }

        return subnet;
    }
    
    /**
     * Wait an instance reaching target {@link InstanceState} by describing the instance every second until it reach
     * target state.
     *
     * @param instanceId
     *            instance's ID
     * @param state
     *            target state
     */
    protected final void waitForState(final String instanceId,
            final InstanceState state) {
        // pass Lone.MAX_VALUE to waitForState make it never time out.
        waitForState(instanceId, state, Long.MAX_VALUE);
    }


    /**
     * Wait an instance reaching target {@link InstanceState} by describing the instance every second until it reach
     * target state or time out.
     *
     * @param instanceId
     *            instance's ID
     * @param state
     *            target state
     * @param timeout
     *            timeout in millisecond
     */
    protected final void waitForState(final String instanceId,
            final InstanceState state, final long timeout) {
        log.info("Wait instance " + instanceId + " reaching state " + state);
        long endTime = System.currentTimeMillis() + timeout;
        while (true) {
            if (System.currentTimeMillis() > endTime) {
                break;
            }

            List<Instance> instances = describeInstances(
                    Arrays.asList(instanceId), false);
            if (instances.get(0).getState().getName().equals(state.getName())) {
                return;
            }
            try {
                Thread.sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Convert a collection of instance IDS into a whitespace-separated String.
     *
     * @param instanceIds
     *            collection of IDs
     * @return whitespace-separated String
     */
    private String toString(final Collection<String> instanceIds) {
        StringBuilder builder = new StringBuilder();
        for (String id : instanceIds) {
            builder.append(" " + id);
        }
        return builder.toString();
    }
}
