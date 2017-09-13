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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AttachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.AttachInternetGatewayResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateInternetGatewayRequest;
import com.amazonaws.services.ec2.model.CreateInternetGatewayResult;
import com.amazonaws.services.ec2.model.CreateRouteRequest;
import com.amazonaws.services.ec2.model.CreateRouteResult;
import com.amazonaws.services.ec2.model.CreateRouteTableRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateSubnetRequest;
import com.amazonaws.services.ec2.model.CreateSubnetResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.ec2.model.CreateVolumeResult;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.CreateVpcResult;
import com.amazonaws.services.ec2.model.DeleteInternetGatewayRequest;
import com.amazonaws.services.ec2.model.DeleteInternetGatewayResult;
import com.amazonaws.services.ec2.model.DeleteRouteTableRequest;
import com.amazonaws.services.ec2.model.DeleteRouteTableResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSubnetRequest;
import com.amazonaws.services.ec2.model.DeleteTagsRequest;
import com.amazonaws.services.ec2.model.DeleteTagsResult;
import com.amazonaws.services.ec2.model.DeleteSubnetResult;
import com.amazonaws.services.ec2.model.DeleteVolumeRequest;
import com.amazonaws.services.ec2.model.DeleteVolumeResult;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DeleteVpcResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
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
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagDescription;
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

// TODO: Auto-generated Javadoc
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

    /** The integration test properties file. */
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
            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.addHeader("region", Regions.US_EAST_1.getName());

            amazonEC2Client = new AmazonEC2Client(credentials, clientConfig);
            amazonEC2Client.setEndpoint(testProperties
                    .getProperty(PROPERTY_ENDPOINT));
        }
    }

    /**
     * Random AMI.
     *
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
     * Describe instances.
     *
     * @param instanceIds            instances' IDs
     * @param enableLogging            log to standard out
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
      * @return list of instances
     */
    protected final List<Instance> describeInstances() {

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult result = amazonEC2Client
                .describeInstances(request);
        List<Instance> instanceList = new ArrayList<Instance>();
        if (result.getReservations().size() > 0) {
	        Assert.assertTrue(result.getReservations().size() > 0);

	        for (Reservation reservation : result.getReservations()) {
	            List<Instance> instances = reservation.getInstances();
	
	            if (null != instances) {
	                for (Instance i : instances) {
	                    instanceList.add(i);
	                }
	            }
	        }
        }
        return instanceList;
    }
    
    /**
     * Describe Images.
     *
      * @return list of Images
     */
    protected final List<Image> describeImages() {

        DescribeImagesRequest request = new DescribeImagesRequest();
        request.withImageIds("ami-12345678");
        DescribeImagesResult result = amazonEC2Client
                .describeImages(request);
        List<Image> instanceList = new ArrayList<Image>();
        if (result.getImages().size() > 0) {
	        Assert.assertTrue(result.getImages().size() > 0);

	        for (Image reservation : result.getImages()) {
	        	instanceList.add(reservation);
	
	          
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
        List<String> stateValues = new ArrayList<String>(
                Arrays.asList(InstanceState.RUNNING.getName(),
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
     * Create VPC.
     *
     * @param cidrBlock the cidr block
     * @param instanceTenancy the instance tenancy
     * @return New vpc
     */
    protected final Vpc createVpc(final String cidrBlock, final String instanceTenancy) {
        CreateVpcRequest req = new CreateVpcRequest();
        req.setCidrBlock(cidrBlock);
        req.setInstanceTenancy(instanceTenancy);
        CreateVpcResult result = amazonEC2Client.createVpc(req);
        return result.getVpc();
    }

    /**
     * delete VPC.
     *
     * @param vpcId the vpc id
     * @return true if delete.
     */
    protected final boolean deleteVpc(final String vpcId) {
        DeleteVpcRequest req = new DeleteVpcRequest();
        req.setVpcId(vpcId);
        DeleteVpcResult result = amazonEC2Client.deleteVpc(req);
        
        if (result != null) {
            return true;
        }

        return false;
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
    * Describe internet gateways.
    *
    * @return List of InternetGateway
    */
    protected final List<InternetGateway> getInternetGateways() {
       List<InternetGateway> internetGateways = null;
       DescribeInternetGatewaysRequest req = new DescribeInternetGatewaysRequest();
       DescribeInternetGatewaysResult result = amazonEC2Client.describeInternetGateways(req);
       if (result != null && !result.getInternetGateways().isEmpty()) {
           internetGateways = result.getInternetGateways();
       }

       return internetGateways;
    }

    /**
     * Create internet gateway.
     *
     * @return InternetGateway
     */
    protected final InternetGateway createInternetGateway() {
        InternetGateway internetGateway = null;

        CreateInternetGatewayRequest req = new CreateInternetGatewayRequest();
        CreateInternetGatewayResult result = amazonEC2Client.createInternetGateway(req);
        if (result != null) {
            internetGateway = result.getInternetGateway();
        }

        return internetGateway;
    }

    /**
     * Attach internet gateway with Vpc.
     *
     * @param internetGatewayId the internet gateway id
     * @param vpcId the vpc id
     * @return true if attach
     */
    protected final boolean attachInternetGateway(final String internetGatewayId, final String vpcId) {
        AttachInternetGatewayRequest req = new AttachInternetGatewayRequest();
        req.setInternetGatewayId(internetGatewayId);
        req.setVpcId(vpcId);
        AttachInternetGatewayResult result = amazonEC2Client.attachInternetGateway(req);
        
        if (result != null) {
            return true;
        }

        return false;
    }
    
    /**
     * Delete internet gateway.
     *
     * @param internetGatewayId the internet gateway id
     * @return true if deleted
     */
    protected final boolean deleteInternetGateway(final String internetGatewayId) {
        DeleteInternetGatewayRequest req = new DeleteInternetGatewayRequest();
        req.setInternetGatewayId(internetGatewayId);
        DeleteInternetGatewayResult result = amazonEC2Client.deleteInternetGateway(req);
        
        if (result != null) {
            return true;
        }

        return false;
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
     * Create route table.
     *
     * @param vpcId the vpc id
     * @return RouteTable
     */
    protected final RouteTable createRouteTable(final String vpcId) {
        RouteTable routeTable = null;

        CreateRouteTableRequest req = new CreateRouteTableRequest();
        req.setVpcId(vpcId);
        CreateRouteTableResult result = amazonEC2Client.createRouteTable(req);
        
        if (result != null) {
            routeTable = result.getRouteTable();
        }

        return routeTable;
    }
    
    /**
     * Create route with gateway and route table.
     *
     * @param routeTableId the route table id
     * @param gatewayId the gateway id
     * @param destinationCidrBlock the destination cidr block
     * @return true if Created Route
     */
    protected final boolean createRoute(final String routeTableId, final String gatewayId, final String destinationCidrBlock) {
      
        CreateRouteRequest req = new CreateRouteRequest();
        req.setDestinationCidrBlock(destinationCidrBlock);
        req.setGatewayId(gatewayId);
        req.setRouteTableId(routeTableId);
        CreateRouteResult result = amazonEC2Client.createRoute(req);
        
        if (result != null) {
            return true;
        }

        return false;
    }
    
    /**
     * Delete route table.
     *
     * @param routeTableId the route table id
     * @return true if deleted
     */
    protected final boolean deleteRouteTable(final String routeTableId) {
        DeleteRouteTableRequest req = new DeleteRouteTableRequest();
        req.setRouteTableId(routeTableId);
        DeleteRouteTableResult result = amazonEC2Client.deleteRouteTable(req);
        
        if (result != null) {
            return true;
        }

        return false;
    }

    /**
     * Describe AvailabilityZone.
     *
     * @return AvailabilityZone
     */
    protected final AvailabilityZone getAvailiablityZones() {
        AvailabilityZone availabilityZone = null;

        DescribeAvailabilityZonesResult result = amazonEC2Client.describeAvailabilityZones();
        if (result != null && !result.getAvailabilityZones().isEmpty()) {
            availabilityZone = result.getAvailabilityZones().get(0);
        }

        return availabilityZone;
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
     * Describe All Volume.
     *
     * @return Collection Volume
     */
    protected final List<Volume> getVolumes() {
        List<Volume> volumes = null;

        DescribeVolumesRequest req = new DescribeVolumesRequest();
        req.setMaxResults(20);
        DescribeVolumesResult result = amazonEC2Client.describeVolumes(req);
        if (result != null && !result.getVolumes().isEmpty()) {
            volumes = result.getVolumes();
            log.info("Page Size : " + volumes.size());
        }

        while(result.getNextToken() != null) { 
            req.setNextToken(result.getNextToken());
            result = amazonEC2Client.describeVolumes(req);
            if (result != null && !result.getVolumes().isEmpty()) {
                 volumes = result.getVolumes();
                 log.info("Page Size : " + volumes.size());
            }
        }
        
        return volumes;
    }

    /**
     * Create Volume.
     *
     * @param availabilityZone the availability zone
     * @param iops the iops
     * @param size the size
     * @param snapshotId the snapshot id
     * @param volumeType the volume type
     * @return Volume
     */
    protected final Volume createVolume(final String availabilityZone, final Integer iops, final Integer size, final String snapshotId, final String volumeType) {
        Volume volume = null;

        CreateVolumeRequest req = new CreateVolumeRequest();
        req.setAvailabilityZone(availabilityZone);
        req.setIops(iops);
        req.setSize(size);
        req.setSnapshotId(snapshotId);
        req.setVolumeType(volumeType);
        
        CreateVolumeResult result = amazonEC2Client.createVolume(req);
        if (result != null) {
            volume = result.getVolume();
        }

        return volume;
    }
    
    /**
     * Create Tags.
     *
     * @param availabilityZone the availability zone
     * @param iops the iops
     * @param size the size
     * @param snapshotId the snapshot id
     * @param volumeType the volume type
     * @return Volume
     */
    protected final boolean createTags(final Collection<String> resources, final Collection<Tag> tags) {
        CreateTagsRequest req = new CreateTagsRequest();
        req.setResources(resources);
        req.setTags(tags);
        
        CreateTagsResult result = amazonEC2Client.createTags(req);
        if (result != null) {
            return true;
        }

        return false;
    }
    
    /**
     * Create Tags.
     *
     * @param availabilityZone the availability zone
     * @param iops the iops
     * @param size the size
     * @param snapshotId the snapshot id
     * @param volumeType the volume type
     * @return Volume
     */
    protected final boolean deleteTags(final Collection<String> resources, final Collection<Tag> tags) {
        DeleteTagsRequest req = new DeleteTagsRequest();
        req.setResources(resources);
        req.setTags(tags);
        
        DeleteTagsResult result = amazonEC2Client.deleteTags(req);
        if (result != null) {
            return true;
        }

        return false;
    }
    
    /**
     * Describe Tags.
     * @return TagsDescription
     */
    protected final List<TagDescription> getTags() {
        DescribeTagsResult result = amazonEC2Client.describeTags();
        List<TagDescription> tagsDesc = null;
        
        if (result != null) {
            
            tagsDesc = result.getTags();
        }

        return tagsDesc;
    }
    
    /**
     * Delete Volume.
     *
     * @param volumeId the volume id
     * @return true if deleted, otherwise false.
     */
    protected final boolean deleteVolume(final String volumeId) {
        DeleteVolumeRequest req = new DeleteVolumeRequest();
        req.setVolumeId(volumeId);
        DeleteVolumeResult result = amazonEC2Client.deleteVolume(req);
        if (result != null) {
            return true;
        }

        return false;
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
     * Describe Subnets.
     * @return List of Subnet
     */
    protected final List<Subnet> getSubnets() {
        List<Subnet> subnets = null;

        DescribeSubnetsRequest req = new DescribeSubnetsRequest();
        DescribeSubnetsResult result = amazonEC2Client.describeSubnets(req);
        if (result != null && !result.getSubnets().isEmpty()) {
            subnets = result.getSubnets();
        }

         return subnets;
      }

    /**
     * Create Subnet.
     *
     * @param cidrBlock the cidr block
     * @param vpcId the vpc id
     * @return Subnet
     */
    protected final Subnet createSubnet(final String cidrBlock, final String vpcId) {
        Subnet subnet = null;

        CreateSubnetRequest req = new CreateSubnetRequest();
        req.setCidrBlock(cidrBlock);
        req.setVpcId(vpcId);
        CreateSubnetResult result = amazonEC2Client.createSubnet(req);
        if (result != null) {
            subnet = result.getSubnet();
        }

        return subnet;
    }
    
    /**
     * Create Security Group.
     *
     * @param groupName the group Name
     * @param groupDescription the group Description
     * @param vpcId vpcId for Sg
     * @return Security Group Id
     */
    protected final String createSecurityGroup(final String groupName, final String groupDescription, final String vpcId) {
        String groupId = null;

        CreateSecurityGroupRequest req = new CreateSecurityGroupRequest();
        req.setGroupName(groupName);
        req.setDescription(groupDescription);
        req.setVpcId(vpcId);
        CreateSecurityGroupResult result = amazonEC2Client.createSecurityGroup(req);
        if (result != null) {
        	groupId = result.getGroupId();
        }

        return groupId;
    }

    /**
     * Delete Subnet.
     *
     * @param subnetId the subnet id
     * @return true if deleted, otherwise false.
     */
    protected final boolean deleteSubnet(final String subnetId) {
        DeleteSubnetRequest req = new DeleteSubnetRequest();
        req.setSubnetId(subnetId);
        DeleteSubnetResult result = amazonEC2Client.deleteSubnet(req);
        if (result != null) {
            return true;
        }
        return false;
     }

    /**
     * Delete SecurityGroup.
     *
     * @param groupId the group id
     * @return true if deleted, otherwise false.
     */
    protected final boolean deleteSecurityGroup(final String groupId) {
        DeleteSecurityGroupRequest req = new DeleteSecurityGroupRequest();
        req.setGroupId(groupId);
        DeleteSecurityGroupResult result = amazonEC2Client.deleteSecurityGroup(req);
        if (result != null) {
            return true;
        }

        /*CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();
        AuthorizeSecurityGroupEgressRequest authorizeSecurityGroupEgressRequest = new AuthorizeSecurityGroupEgressRequest();
        authorizeSecurityGroupEgressRequest.setIpProtocol(ipProtocol);
        CreateSecurityGroupResult result = amazonEC2Client.authorizeSecurityGroupEgress(authorizeSecurityGroupEgressRequest);*/
        return false;
    }

    /**
     * Authorize SecurityGroup Ingress.
     * @param groupId the group id
     * @param ipProtocol ipProtocol for Ingress.
     * @param port portRange for Ingress.
     * @param cidrIp cidr Ip for Ingress
     * @return true if deleted, otherwise false.
     */
    protected final boolean authorizeSecurityGroupIngress(final String groupId, final String ipProtocol, final Integer port, final String cidrIp) {
    	AuthorizeSecurityGroupIngressRequest req = new AuthorizeSecurityGroupIngressRequest();
        req.setGroupId(groupId);
        req.setCidrIp(cidrIp);
        req.setFromPort(port);
        req.setToPort(port);
        req.setIpProtocol(ipProtocol);
        AuthorizeSecurityGroupIngressResult result = amazonEC2Client.authorizeSecurityGroupIngress(req);
        if (result != null) {
            return true;
        }

        return false;
    }

    /**
     * Authorize SecurityGroup Egress.
     * @param groupId the group id
     * @param ipProtocol ipProtocol for Egress.
     * @param port portRange for Egress.
     * @param cidrIp cidr Ip for Egress
     * @return true if deleted, otherwise false.
     */
    protected final boolean authorizeSecurityGroupEgress(final String groupId, final String ipProtocol, final Integer port, final String cidrIp) {
    	AuthorizeSecurityGroupEgressRequest req = new AuthorizeSecurityGroupEgressRequest();
        req.setGroupId(groupId);
        req.setCidrIp(cidrIp);
        req.setFromPort(port);
        req.setToPort(port);
        req.setIpProtocol(ipProtocol);
        AuthorizeSecurityGroupEgressResult result = amazonEC2Client.authorizeSecurityGroupEgress(req);
        if (result != null) {
            return true;
        }

        return false;
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
