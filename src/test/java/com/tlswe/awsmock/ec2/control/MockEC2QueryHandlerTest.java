package com.tlswe.awsmock.ec2.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseItemType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeInternetGatewaysResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeRouteTablesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeSecurityGroupsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeVpcsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.InternetGatewayType;
import com.tlswe.awsmock.ec2.cxf_generated.IpPermissionType;
import com.tlswe.awsmock.ec2.cxf_generated.ReservationInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.RouteTableType;
import com.tlswe.awsmock.ec2.cxf_generated.RunInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesItemType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesSetType;
import com.tlswe.awsmock.ec2.cxf_generated.SecurityGroupItemType;
import com.tlswe.awsmock.ec2.cxf_generated.VpcType;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;
import com.tlswe.awsmock.ec2.util.JAXBUtil;
import com.tlswe.example.CustomMockEc2Instance;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEC2QueryHandler.class, MockEc2Controller.class, JAXBUtil.class })
public class MockEC2QueryHandlerTest {

    private static Properties properties = new Properties();
    private static final String INVALID_QUERY = "<Code>InvalidQuery</Code>";
    private static final String NO_PARAM_IN_QUERY = "No parameter in query at all!";
    private static final String NO_VERSION_IN_QUERY = "There should be a parameter of &apos;Version&apos; provided in the query!";
    private static final String NO_ACTION_IN_QUERY = "There should be a parameter of &apos;Action&apos; provided in the query!";
    private static final String DUMMY_XML_RESPONSE = "Dummy XML Response";
    private static final String ACTION_KEY = "Action";
    private static final String VERSION_KEY = "Version";
    private static final String VERSION_1 = "version1";

    static {
        InputStream inputStream = null;

        // first load default properties from aws-mock-default.properties
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(PropertiesUtils.FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES);
        if (null == inputStream) {
            // do nothing
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // then load user-defined overriding properties from aws-mock.properties if it exists in classpath
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(PropertiesUtils.FILE_NAME_AWS_MOCK_PROPERTIES);
        if (null == inputStream) {
            // do nothing
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Before
    public void doSetup() {
        PowerMockito.mockStatic(JAXBUtil.class);
    }


    @Test
    public void Test_getInstance() {
        Assert.assertTrue(MockEC2QueryHandler.getInstance() != null);
    }


    @Test
    public void Test_getXmlError() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        String output = Whitebox.invokeMethod(handler, "getXmlError", "101", "Error had taken place!");

        // check that the template file is populated
        Assert.assertTrue(output != null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error had taken place!</Message>"));

    }


    @Test
    public void Test_describeVpcs() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeVpcsResponseType vpcsResponseType = Whitebox.invokeMethod(handler, "describeVpcs");

        Assert.assertTrue(vpcsResponseType != null);
        Assert.assertTrue(vpcsResponseType.getVpcSet().getItem().size() == 1); // has one VPC

        VpcType vpcType = vpcsResponseType.getVpcSet().getItem().get(0);
        Assert.assertTrue(vpcType.getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID))); // from
                                                                                                  // aws.mock-default.properties
        Assert.assertTrue(vpcType.getState().equals(properties.get(Constants.PROP_NAME_VPC_STATE)));
        Assert.assertTrue(vpcType.getCidrBlock().equals(properties.get(Constants.PROP_NAME_CIDR_BLOCK)));
    }


    @Test
    public void Test_describeSecurityGroups() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeSecurityGroupsResponseType describeSecurityGroupsResponseType = Whitebox.invokeMethod(handler,
                "describeSecurityGroups");

        Assert.assertTrue(describeSecurityGroupsResponseType != null);
        Assert.assertTrue(describeSecurityGroupsResponseType.getSecurityGroupInfo().getItem().size() == 1); // has one
                                                                                                            // Security
                                                                                                            // Group

        SecurityGroupItemType securityGroupItem = describeSecurityGroupsResponseType.getSecurityGroupInfo().getItem()
                .get(0);
        Assert.assertTrue(securityGroupItem.getGroupId().equals(properties.get(Constants.PROP_NAME_SECURITY_GROUP_ID)));
        Assert.assertTrue(securityGroupItem.getGroupName().equals(
                properties.get(Constants.PROP_NAME_SECURITY_GROUP_NAME)));
        Assert.assertTrue(securityGroupItem.getOwnerId().equals(properties.get(Constants.PROP_NAME_SECURITY_OWNER_ID)));
        Assert.assertTrue(securityGroupItem.getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID)));

        IpPermissionType ipPermission = securityGroupItem.getIpPermissions().getItem().get(0);
        Assert.assertTrue(ipPermission.getIpProtocol().equals(properties.get(Constants.PROP_NAME_IP_PROTOCOL)));
        Assert.assertTrue(ipPermission.getFromPort().equals(
                Integer.parseInt((String) properties.get(Constants.PROP_NAME_SOURCE_PORT))));
        Assert.assertTrue(ipPermission.getToPort().equals(
                Integer.parseInt((String) properties.get(Constants.PROP_NAME_DEST_PORT))));

    }


    @Test
    public void Test_describeInternetGateways() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeInternetGatewaysResponseType describeInternetGatewaysResponseType = Whitebox.invokeMethod(handler,
                "describeInternetGateways");

        Assert.assertTrue(describeInternetGatewaysResponseType != null);
        Assert.assertTrue(describeInternetGatewaysResponseType.getInternetGatewaySet().getItem().size() == 1);

        InternetGatewayType internetGateway = describeInternetGatewaysResponseType.getInternetGatewaySet().getItem()
                .get(0);
        Assert.assertTrue(internetGateway.getInternetGatewayId().equals(properties.get(Constants.PROP_NAME_GATEWAY_ID)));

    }


    @Test
    public void Test_describeRouteTables() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeRouteTablesResponseType describeRouteTablesResponseType = Whitebox.invokeMethod(handler,
                "describeRouteTables");

        Assert.assertTrue(describeRouteTablesResponseType != null);
        Assert.assertTrue(describeRouteTablesResponseType.getRouteTableSet().getItem().size() == 1);

        RouteTableType routeTableSetType = describeRouteTablesResponseType.getRouteTableSet().getItem().get(0);
        Assert.assertTrue(routeTableSetType.getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID)));
        Assert.assertTrue(routeTableSetType.getRouteTableId()
                .equals(properties.get(Constants.PROP_NAME_ROUTE_TABLE_ID)));

    }


    @Test
    public void Test_describeImages() throws Exception {

        Set<String> MOCK_AMIS = new TreeSet<String>();
        MOCK_AMIS.add("ami-1");
        MOCK_AMIS.add("ami-2");

        DescribeImagesResponseItemType item1 = new DescribeImagesResponseItemType();
        item1.setImageId("ami-1");

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        // Use reflection to attach a fake set of AMIs to the private static final variable
        Field f = MockEC2QueryHandler.class.getDeclaredField("MOCK_AMIS");
        f.setAccessible(true);
        f.set(MockEC2QueryHandler.class, MOCK_AMIS);

        DescribeImagesResponseType describeImagesResponseType = Whitebox.invokeMethod(handler, "describeImages");
        Assert.assertTrue(describeImagesResponseType != null);

        DescribeImagesResponseInfoType describeImagesResponseInfoType = describeImagesResponseType.getImagesSet();

        Assert.assertTrue(describeImagesResponseInfoType.getItem().size() == 2);

        boolean hasAMI1 = false, hasAMI2 = false;

        for (DescribeImagesResponseItemType item : describeImagesResponseInfoType.getItem()) {
            if ("ami-1".equals(item.getImageId())) {
                hasAMI1 = true;
            }
            if ("ami-2".equals(item.getImageId())) {
                hasAMI2 = true;
            }
        }

        Assert.assertTrue(hasAMI1 && hasAMI2);

        // reset the value of this field
        f.set(MockEC2QueryHandler.class, new TreeSet<String>());

    }


    @Test
    public void Test_termianteInstances() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        CustomMockEc2Instance ec2Mocked1 = new CustomMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        CustomMockEc2Instance ec2Mocked2 = new CustomMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Whitebox.setInternalState(handler, "mockEc2Controller", controller);
        Whitebox.invokeMethod(handler, "terminateInstances", instanceIDs);
        Assert.assertTrue(ec2Mocked1.isTerminated());
        Assert.assertTrue(ec2Mocked2.isTerminated());
    }


    @Test
    public void Test_stopInstances() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        CustomMockEc2Instance ec2Mocked1 = new CustomMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        CustomMockEc2Instance ec2Mocked2 = new CustomMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Whitebox.setInternalState(handler, "mockEc2Controller", controller);

        controller.startInstances(instanceIDs); // first we need to start to bring to booting or starting state

        Whitebox.invokeMethod(handler, "stopInstances", instanceIDs);
        Assert.assertTrue(ec2Mocked1.isStopping());
        Assert.assertTrue(ec2Mocked2.isStopping());
    }


    @Test
    public void Test_startInstances() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add("ec2Mocked1");
        instanceIDs.add("ec2Mocked2");

        CustomMockEc2Instance ec2Mocked1 = new CustomMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        CustomMockEc2Instance ec2Mocked2 = new CustomMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked1").thenReturn(ec2Mocked1);
        PowerMockito.when(controller, "getMockEc2Instance", "ec2Mocked2").thenReturn(ec2Mocked2);

        Whitebox.setInternalState(handler, "mockEc2Controller", controller);

        Whitebox.invokeMethod(handler, "startInstances", instanceIDs);
        Assert.assertTrue(ec2Mocked1.isBooting());
        Assert.assertTrue(ec2Mocked2.isBooting());
    }


    @Test
    public void Test_runInstances() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);
        Whitebox.setInternalState(handler, "mockEc2Controller", controller);

        RunInstancesResponseType ret = Whitebox.invokeMethod(handler, "runInstances", "ami-1",
                InstanceType.C1_MEDIUM.getName(), 1, 1);

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getInstancesSet().getItem().size() == 1);

        RunningInstancesItemType instItem = ret.getInstancesSet().getItem().get(0);
        Assert.assertTrue(instItem.getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID))); // from
                                                                                                   // aws.mock-default.properties
        Assert.assertTrue(instItem.getSubnetId().equals(properties.get(Constants.PROP_NAME_SUBNET_ID)));
        Assert.assertTrue(instItem.getPrivateIpAddress().equals(properties.get(Constants.PROP_NAME_PRIVATE_IP_ADDRESS)));
        Assert.assertTrue(instItem.getImageId().equals("ami-1"));
        Assert.assertTrue(instItem.getInstanceId() != null);

    }


    @Test
    public void Test_describeInstances() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        CustomMockEc2Instance ec2Mocked1 = new CustomMockEc2Instance();
        ec2Mocked1.setInstanceType(InstanceType.C1_MEDIUM);

        CustomMockEc2Instance ec2Mocked2 = new CustomMockEc2Instance();
        ec2Mocked2.setInstanceType(InstanceType.C3_8XLARGE);

        MockEc2Controller controller = Mockito.spy(MockEc2Controller.class);

        Map<String, AbstractMockEc2Instance> allMockEc2Instances = new ConcurrentHashMap<String, AbstractMockEc2Instance>();
        allMockEc2Instances.put(ec2Mocked1.getInstanceID(), ec2Mocked1);
        allMockEc2Instances.put(ec2Mocked2.getInstanceID(), ec2Mocked2);

        Set<String> instanceIDs = new HashSet<String>();
        instanceIDs.add(ec2Mocked1.getInstanceID());
        instanceIDs.add(ec2Mocked2.getInstanceID());

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller, allMockEc2Instances);
        Whitebox.setInternalState(handler, "mockEc2Controller", controller);

        Set<String> instanceStateSet = new HashSet<String>();
        instanceStateSet.add(InstanceState.STOPPED.getName());

        DescribeInstancesResponseType ret = Whitebox.invokeMethod(handler, "describeInstances", instanceIDs,
                instanceStateSet, null, 0);

        // both of the instances should be returned as they are in stopped state
        Assert.assertTrue(ret.getReservationSet().getItem().size() == 2);

        List<ReservationInfoType> reservationList = ret.getReservationSet().getItem();

        // check if ownerId is default
        Assert.assertTrue(reservationList.get(0).getOwnerId().equals("mock-owner"));
        Assert.assertTrue(reservationList.get(1).getOwnerId().equals("mock-owner"));

        // check if gorupId is default
        Assert.assertTrue(reservationList.get(0).getGroupSet().getItem().get(0).getGroupId().equals("default"));
        Assert.assertTrue(reservationList.get(1).getGroupSet().getItem().get(0).getGroupId().equals("default"));

        RunningInstancesSetType runningSetType = ret.getReservationSet().getItem().get(0).getInstancesSet();

        String instanceId1 = runningSetType.getItem().get(0).getInstanceId();

        // check if default params were applied
        Assert.assertTrue(runningSetType.getItem().get(0).getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getPrivateIpAddress()
                .equals(properties.get(Constants.PROP_NAME_PRIVATE_IP_ADDRESS)));
        Assert.assertTrue(runningSetType.getItem().get(0).getSubnetId()
                .equals(properties.get(Constants.PROP_NAME_SUBNET_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getInstanceState().getName()
                .equals(InstanceState.STOPPED.getName()));

        runningSetType = ret.getReservationSet().getItem().get(1).getInstancesSet();

        String instanceId2 = runningSetType.getItem().get(0).getInstanceId();

        // check if default params were applied
        Assert.assertTrue(runningSetType.getItem().get(0).getVpcId().equals(properties.get(Constants.PROP_NAME_VPC_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getPrivateIpAddress()
                .equals(properties.get(Constants.PROP_NAME_PRIVATE_IP_ADDRESS)));
        Assert.assertTrue(runningSetType.getItem().get(0).getSubnetId()
                .equals(properties.get(Constants.PROP_NAME_SUBNET_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getInstanceState().getName()
                .equals(InstanceState.STOPPED.getName()));

        // check if the instances are not equal and properly filtered
        Assert.assertFalse(instanceId1.equals(instanceId2));
        Assert.assertTrue(instanceIDs.contains(instanceId1));
        Assert.assertTrue(instanceIDs.contains(instanceId2));

        // test the pagination functionality
        ret = Whitebox.invokeMethod(handler, "describeInstances", new HashSet<String>(), instanceStateSet, null, 1);

        Assert.assertTrue(ret.getReservationSet().getItem().size() == 1);

        String token = ret.getNextToken();
        Assert.assertTrue(token != null);

        // expectedException.expect(BadEc2RequestException.class);
        Throwable e = null;
        try {
            Whitebox.invokeMethod(handler, "describeInstances", instanceIDs, instanceStateSet, token, 0);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue(null != e);
        Assert.assertTrue(e instanceof BadEc2RequestException);
        Assert.assertTrue(e.getMessage().contains(
                "AWS Error Message: The parameter instancesSet cannot be used with the parameter nextToken"));

        e = null;
        try {
            Whitebox.invokeMethod(handler, "describeInstances", new TreeSet<String>(), instanceStateSet,
                    "invalid-string", 0);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue(null != e);
        Assert.assertTrue(e instanceof BadEc2RequestException);
        Assert.assertTrue(e.getMessage().contains(
                "AWS Error Message: Unable to parse pagination token"));

        e = null;
        try {
            Whitebox.invokeMethod(handler, "describeInstances", instanceIDs, instanceStateSet, null, 100);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue(null != e);
        Assert.assertTrue(e instanceof BadEc2RequestException);
        Assert.assertTrue(e.getMessage().contains(
                "AWS Error Message: The parameter instancesSet cannot be used with the parameter maxResults"));

        // get the second instance using next token
        ret = Whitebox.invokeMethod(handler, "describeInstances", new TreeSet<String>(), instanceStateSet, token, 0);
        Assert.assertTrue(ret.getReservationSet().getItem().size() == 1);
        Assert.assertTrue(ret.getNextToken() == null);

    }


    @Test
    public void Test_parseInstanceIDs() throws Exception {

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("InstanceId.1", new String[] { "i-fd5bfd2" });
        queryParams.put("InstanceId.2", new String[] { "i-fd5bfd3" });
        queryParams.put("dummy", new String[] { "i-dummy" }); // should not be retrieved

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        Set<String> instanceIDs = Whitebox.invokeMethod(handler, "parseInstanceIDs", queryParams);

        Assert.assertTrue(instanceIDs.contains("i-fd5bfd2"));
        Assert.assertTrue(instanceIDs.contains("i-fd5bfd3"));
        Assert.assertFalse(instanceIDs.contains("i-dummy"));
    }


    @Test
    public void Test_parseInstanceStates() throws Exception {

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("Filter.1.Value.1", new String[] { "running" });
        queryParams.put("Filter.1.Value.2", new String[] { "pending" });
        queryParams.put("Filter.1.Dummy", new String[] { "none" });

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        Set<String> statesSet = Whitebox.invokeMethod(handler, "parseInstanceStates", queryParams);

        Assert.assertTrue(statesSet.contains("running"));
        Assert.assertTrue(statesSet.contains("pending"));
        Assert.assertFalse(statesSet.contains("none"));
    }


    @Test
    public void Test_handleNoParams() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(null, null); // does nothing

        handler.handle(null, response); // no query params

        String responseString = sw.toString();
        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_PARAM_IN_QUERY));

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        handler.handle(new HashMap<String, String[]>(), response); // no query params

        responseString = sw.toString();
        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_PARAM_IN_QUERY));
    }


    @Test
    public void Test_handleImproperVersionParams() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        // no version key here
        queryParams.put("someKey", new String[] { "someValue" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_VERSION_IN_QUERY));

        // more than two version values here
        queryParams.put(VERSION_KEY, new String[] { VERSION_1, "version2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, response);

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_VERSION_IN_QUERY));

    }


    @Test
    public void Test_handleImproperActionParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        // no action key provided
        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_ACTION_IN_QUERY));

        // more than two action values here
        queryParams.put(ACTION_KEY, new String[] { "action1", "action2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, response);

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_ACTION_IN_QUERY));
    }


    @Test
    public void Test_handleUnsupportedActionParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "unsupportedAction" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains("NotImplementedAction"));

    }


    @Test
    public void Test_handleDescribeInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeInstancesResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeInstances" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleStartInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("StartInstancesResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "StartInstances" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleStopInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("StopInstancesResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "StopInstances" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleTerminateInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("TerminateInstancesResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "TerminateInstances" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleDescribeVpcs() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVpcsResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVpcs" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleDescribeSecurityGroups() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeSecurityGroupsResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeSecurityGroups" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleDescribeInternetGateways() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeInternetGatewaysResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeInternetGateways" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }


    @Test
    public void Test_handleDescribeRouteTables() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeRouteTablesResponse"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeRouteTables" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleDescribeSubnets() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeSubnetsResponseType"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeSubnets" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeVolumes() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVolumesResponseType"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVolumes" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
}
