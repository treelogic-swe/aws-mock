package com.tlswe.awsmock.ec2.control;

import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.cxf_generated.*;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;
import com.tlswe.awsmock.ec2.util.JAXBUtil;
import com.tlswe.example.CustomMockEc2Instance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEC2QueryHandler.class, MockEc2Controller.class, MockInternetGatewayController.class, MockRouteTableController.class,
    MockSubnetController.class, MockVolumeController.class, MockVpcController.class, MockTagsController.class, MockSecurityGroupController.class,
    JAXBUtil.class })
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*" })
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
        String output = Whitebox.invokeMethod(handler, "getXmlError", "101",
                "Error had taken place!");

        // check that the template file is populated
        Assert.assertTrue(output != null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error had taken place!</Message>"));

    }

    @Test
    public void Test_describeVpcs() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType retCreate = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(retCreate != null);
        Assert.assertTrue(retCreate.getVpc().getVpcId() != null);
        DescribeVpcsResponseType vpcsResponseType = Whitebox.invokeMethod(handler, "describeVpcs");

        Assert.assertTrue(vpcsResponseType != null);
        Assert.assertTrue(vpcsResponseType.getVpcSet().getItem().size() == 1); // has one VPC

        VpcType vpcType = vpcsResponseType.getVpcSet().getItem().get(0);
        Assert.assertTrue(
                vpcType.getCidrBlock().equals(properties.get(Constants.PROP_NAME_CIDR_BLOCK)));
    }

    @Test
    public void Test_describeSecurityGroups() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        
        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);
        
        
        MockSecurityGroupController controllerSG = Mockito.spy(MockSecurityGroupController.class);
        Whitebox.setInternalState(handler, "mockSecurityGroupController", controllerSG);

        CreateSecurityGroupResponseType retSg = Whitebox.invokeMethod(handler, "createSecurityGroup", "SgName", "SgDesc", ret.getVpc().getVpcId());

        Assert.assertTrue(retSg != null);
        Assert.assertTrue(retSg.getGroupId() != null);

        // Ingress
        Whitebox.invokeMethod(handler, "authorizeSecurityGroupIngress", retSg.getGroupId(),  "TCP", 22, 22, properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        // Egress
        Whitebox.invokeMethod(handler, "authorizeSecurityGroupEgress", retSg.getGroupId(),  "TCP", 22, 22, properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        DescribeSecurityGroupsResponseType describeSecurityGroupsResponseType = Whitebox
                .invokeMethod(handler,
                        "describeSecurityGroups");

        Assert.assertTrue(describeSecurityGroupsResponseType != null);
        Assert.assertTrue(
                describeSecurityGroupsResponseType.getSecurityGroupInfo().getItem().size() == 1); // has one
    }

    @Test
    public void Test_describeInternetGateways() throws Exception {

    	 MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

         MockVpcController controller = Mockito.spy(MockVpcController.class);
         Whitebox.setInternalState(handler, "mockVpcController", controller);

         CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                 "Default");

         Assert.assertTrue(ret != null);
         Assert.assertTrue(ret.getVpc().getVpcId() != null);

         MockInternetGatewayController controllerInternetGt = Mockito.spy(MockInternetGatewayController.class);
         Whitebox.setInternalState(handler, "mockInternetGatewayController", controllerInternetGt);

         CreateInternetGatewayResponseType retGatewary = Whitebox.invokeMethod(handler, "createInternetGateway");

         Assert.assertTrue(retGatewary != null);
         Assert.assertTrue(retGatewary.getInternetGateway().getInternetGatewayId() != null);

         //Attach Gateway
         Whitebox.invokeMethod(handler, "attachInternetGateway", retGatewary.getInternetGateway().getInternetGatewayId(),  ret.getVpc().getVpcId());

        
        DescribeInternetGatewaysResponseType describeInternetGatewaysResponseType = Whitebox
                .invokeMethod(handler,
                        "describeInternetGateways");

        Assert.assertTrue(describeInternetGatewaysResponseType != null);
        Assert.assertTrue(
                describeInternetGatewaysResponseType.getInternetGatewaySet().getItem().size() == 1);

        InternetGatewayType internetGateway = describeInternetGatewaysResponseType
                .getInternetGatewaySet().getItem()
                .get(0);
        Assert.assertTrue(internetGateway.getInternetGatewayId() != null);

    }

    @Test
    public void Test_describeRouteTables() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        MockRouteTableController controllerRT = Mockito.spy(MockRouteTableController.class);
        Whitebox.setInternalState(handler, "mockRouteTableController", controllerRT);

        CreateRouteTableResponseType retRt = Whitebox.invokeMethod(handler, "createRouteTable", ret.getVpc().getVpcId(), properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        Assert.assertTrue(retRt != null);
        Assert.assertTrue(retRt.getRouteTable().getRouteTableId() != null);

        DescribeRouteTablesResponseType describeRouteTablesResponseType = Whitebox.invokeMethod(
                handler,
                "describeRouteTables");

        Assert.assertTrue(describeRouteTablesResponseType != null);
        Assert.assertTrue(describeRouteTablesResponseType.getRouteTableSet().getItem().size() == 1);
    }

    @Test
    public void Test_describeSubnets() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        MockSubnetController controllerSubnet = Mockito.spy(MockSubnetController.class);
        Whitebox.setInternalState(handler, "mockSubnetController", controllerSubnet);

        CreateSubnetResponseType retRt = Whitebox.invokeMethod(handler, "createSubnet", ret.getVpc().getVpcId(), properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        Assert.assertTrue(retRt != null);
        Assert.assertTrue(retRt.getSubnet().getSubnetId() != null);

        DescribeSubnetsResponseType describeRouteTablesResponseType = Whitebox.invokeMethod(
                handler,
                "describeSubnets");

        Assert.assertTrue(describeRouteTablesResponseType != null);
        Assert.assertTrue(describeRouteTablesResponseType.getSubnetSet().getItem().size() == 1);
    }

    
    @Test
    public void Test_describeVolumes() throws Exception {
    	MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
    	MockVolumeController controller = Mockito.spy(MockVolumeController.class);
        Whitebox.setInternalState(handler, "mockVolumeController", controller);

        CreateVolumeResponseType retCreate = Whitebox.invokeMethod(handler, "createVolume", "VolumeType", "23GB", "us-east-1a", 23, "2323");

        Assert.assertTrue(retCreate != null);
        Assert.assertTrue(retCreate.getVolumeId() != null);
        DescribeVolumesResponseType volumeResponseType = Whitebox.invokeMethod(handler, "describeVolumes", "", 50);

        Assert.assertTrue(volumeResponseType != null);
        Assert.assertTrue(volumeResponseType.getVolumeSet().getItem().size() == 1); // has one VPC

        Assert.assertTrue(volumeResponseType.getVolumeSet().getItem().get(0).getVolumeType() != null);
        
        DeleteVolumeResponseType deleteVolumeResponseType = Whitebox.invokeMethod(handler, "deleteVolume", volumeResponseType.getVolumeSet().getItem().get(0).getVolumeId());
        Assert.assertTrue(deleteVolumeResponseType != null);
    }

    @Test
    public void Test_describeAvailabilityZones() throws Exception {
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeAvailabilityZonesResponseType describeAvailabilityZonesResponseType = Whitebox
                .invokeMethod(handler,
                        "describeAvailabilityZones");

        Assert.assertTrue(describeAvailabilityZonesResponseType != null);
        Assert.assertTrue(describeAvailabilityZonesResponseType.getAvailabilityZoneInfo().getItem()
                .size() == 1);

        AvailabilityZoneItemType availabilityZoneItemType = describeAvailabilityZonesResponseType
                .getAvailabilityZoneInfo().getItem().get(0);
        Assert.assertTrue(availabilityZoneItemType.getRegionName() != null);
    }

    @Test
    public void Test_describeImagesWithParams() throws Exception {

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
        
        DescribeImagesResponseType describeImagesResponseType = Whitebox.invokeMethod(handler,
                "describeImages", new TreeSet<String>());
        Assert.assertTrue(describeImagesResponseType != null);

        DescribeImagesResponseInfoType describeImagesResponseInfoType = describeImagesResponseType
                .getImagesSet();

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

        DescribeImagesResponseType describeImagesResponseType = Whitebox.invokeMethod(handler,
                "describeImages", MOCK_AMIS);
        Assert.assertTrue(describeImagesResponseType != null);

        DescribeImagesResponseInfoType describeImagesResponseInfoType = describeImagesResponseType
                .getImagesSet();

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
        Assert.assertTrue(
                instItem.getSubnetId().equals(properties.get(Constants.PROP_NAME_SUBNET_ID)));
        Assert.assertTrue(instItem.getPrivateIpAddress()
                .equals(properties.get(Constants.PROP_NAME_PRIVATE_IP_ADDRESS)));
        Assert.assertTrue(instItem.getImageId().equals("ami-1"));
        Assert.assertTrue(instItem.getInstanceId() != null);

    }

    @Test
    public void Test_createAndDeleteVpc() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        DeleteVpcResponseType retDelete = Whitebox.invokeMethod(handler, "deleteVpc", ret.getVpc().getVpcId());

        Assert.assertTrue(retDelete != null);
    }

    @Test
    public void Test_createAndDeleteRouteTable() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        MockRouteTableController controllerRT = Mockito.spy(MockRouteTableController.class);
        Whitebox.setInternalState(handler, "mockRouteTableController", controllerRT);

        CreateRouteTableResponseType retRt = Whitebox.invokeMethod(handler, "createRouteTable", ret.getVpc().getVpcId(), properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        Assert.assertTrue(retRt != null);
        Assert.assertTrue(retRt.getRouteTable().getRouteTableId() != null);

        DeleteRouteTableResponseType retRtDelete = Whitebox.invokeMethod(handler, "deleteRouteTable", retRt.getRouteTable().getRouteTableId());

        Assert.assertTrue(retRtDelete != null);
    }

    @Test
    public void Test_createAndDeleteInternetGateway() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockInternetGatewayController controller = Mockito.spy(MockInternetGatewayController.class);
        Whitebox.setInternalState(handler, "mockInternetGatewayController", controller);

        CreateInternetGatewayResponseType ret = Whitebox.invokeMethod(handler, "createInternetGateway");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getInternetGateway().getInternetGatewayId() != null);

        DeleteInternetGatewayResponseType retDelete = Whitebox.invokeMethod(handler, "deleteInternetGateway", ret.getInternetGateway().getInternetGatewayId() );

        Assert.assertTrue(retDelete != null);
    }

    @Test
    public void Test_createAndDeleteTags() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockTagsController controller = Mockito.spy(MockTagsController.class);
        Whitebox.setInternalState(handler, "mockTagsController", controller);

        Collection<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("tag1", "value1");
        tags.put("tag2", "value2");

        CreateTagsResponseType ret = Whitebox.invokeMethod(handler, "createTags", resources, tags);

        Assert.assertTrue(ret != null);

        DeleteTagsResponseType retDelete = Whitebox.invokeMethod(handler, "deleteTags", resources);

        Assert.assertTrue(retDelete != null);
    }

    @Test
    public void Test_describeTags() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockTagsController controller = Mockito.spy(MockTagsController.class);
        Whitebox.setInternalState(handler, "mockTagsController", controller);

        Collection<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("tag1", "value1");
        tags.put("tag2", "value2");

        CreateTagsResponseType ret = Whitebox.invokeMethod(handler, "createTags", resources, tags);

        Assert.assertTrue(ret != null);

        DescribeTagsResponseType retDelete = Whitebox.invokeMethod(handler, "describeTags");

        Assert.assertTrue(retDelete != null);
    }

    @Test
    public void Test_createAndAttachAndDeleteInternetGateway() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        MockInternetGatewayController controllerInternetGt = Mockito.spy(MockInternetGatewayController.class);
        Whitebox.setInternalState(handler, "mockInternetGatewayController", controllerInternetGt);

        CreateInternetGatewayResponseType retGatewary = Whitebox.invokeMethod(handler, "createInternetGateway");

        Assert.assertTrue(retGatewary != null);
        Assert.assertTrue(retGatewary.getInternetGateway().getInternetGatewayId() != null);

        //Attach Gateway
        Whitebox.invokeMethod(handler, "attachInternetGateway", retGatewary.getInternetGateway().getInternetGatewayId(),  ret.getVpc().getVpcId());

        DeleteInternetGatewayResponseType retDelete = Whitebox.invokeMethod(handler, "deleteInternetGateway", retGatewary.getInternetGateway().getInternetGatewayId() );

        Assert.assertTrue(retDelete != null);
    }

    @Test
    public void Test_createRouteInternetGateway() throws Exception {

        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        MockVpcController controller = Mockito.spy(MockVpcController.class);
        Whitebox.setInternalState(handler, "mockVpcController", controller);

        CreateVpcResponseType ret = Whitebox.invokeMethod(handler, "createVpc", properties.get(Constants.PROP_NAME_CIDR_BLOCK),
                "Default");

        Assert.assertTrue(ret != null);
        Assert.assertTrue(ret.getVpc().getVpcId() != null);

        MockRouteTableController controllerRT = Mockito.spy(MockRouteTableController.class);
        Whitebox.setInternalState(handler, "mockRouteTableController", controllerRT);

        CreateRouteTableResponseType retRt = Whitebox.invokeMethod(handler, "createRouteTable", ret.getVpc().getVpcId(), properties.get(Constants.PROP_NAME_CIDR_BLOCK));

        Assert.assertTrue(retRt != null);
        Assert.assertTrue(retRt.getRouteTable().getRouteTableId() != null);

        MockInternetGatewayController controllerInternetGt = Mockito.spy(MockInternetGatewayController.class);
        Whitebox.setInternalState(handler, "mockInternetGatewayController", controllerInternetGt);

        CreateInternetGatewayResponseType retGatewary = Whitebox.invokeMethod(handler, "createInternetGateway");

        Assert.assertTrue(retGatewary != null);
        Assert.assertTrue(retGatewary.getInternetGateway().getInternetGatewayId() != null);

        Whitebox.setInternalState(handler, "mockRouteTableController", controllerRT);
        Whitebox.invokeMethod(handler, "createRoute", properties.get(Constants.PROP_NAME_CIDR_BLOCK), retGatewary.getInternetGateway().getInternetGatewayId(), retRt.getRouteTable().getRouteTableId());
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

        MemberModifier.field(MockEc2Controller.class, "allMockEc2Instances").set(controller,
                allMockEc2Instances);
        Whitebox.setInternalState(handler, "mockEc2Controller", controller);

        Set<String> instanceStateSet = new HashSet<String>();
        instanceStateSet.add(InstanceState.STOPPED.getName());

        DescribeInstancesResponseType ret = Whitebox.invokeMethod(handler, "describeInstances",
                instanceIDs,
                instanceStateSet, null, 0);

        // both of the instances should be returned as they are in stopped state
        Assert.assertTrue(ret.getReservationSet().getItem().size() == 2);

        List<ReservationInfoType> reservationList = ret.getReservationSet().getItem();

        // check if ownerId is default
        Assert.assertTrue(reservationList.get(0).getOwnerId().equals("mock-owner"));
        Assert.assertTrue(reservationList.get(1).getOwnerId().equals("mock-owner"));

        // check if gorupId is default
        Assert.assertTrue(reservationList.get(0).getGroupSet().getItem().get(0).getGroupId()
                .equals("default"));
        Assert.assertTrue(reservationList.get(1).getGroupSet().getItem().get(0).getGroupId()
                .equals("default"));

        RunningInstancesSetType runningSetType = ret.getReservationSet().getItem().get(0)
                .getInstancesSet();

        String instanceId1 = runningSetType.getItem().get(0).getInstanceId();

        // check if default params were applied
        Assert.assertTrue(runningSetType.getItem().get(0).getVpcId()
                .equals(properties.get(Constants.PROP_NAME_VPC_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getPrivateIpAddress()
                .equals(properties.get(Constants.PROP_NAME_PRIVATE_IP_ADDRESS)));
        Assert.assertTrue(runningSetType.getItem().get(0).getSubnetId()
                .equals(properties.get(Constants.PROP_NAME_SUBNET_ID)));
        Assert.assertTrue(runningSetType.getItem().get(0).getInstanceState().getName()
                .equals(InstanceState.STOPPED.getName()));

        runningSetType = ret.getReservationSet().getItem().get(1).getInstancesSet();

        String instanceId2 = runningSetType.getItem().get(0).getInstanceId();

        // check if default params were applied
        Assert.assertTrue(runningSetType.getItem().get(0).getVpcId()
                .equals(properties.get(Constants.PROP_NAME_VPC_ID)));
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
        ret = Whitebox.invokeMethod(handler, "describeInstances", new HashSet<String>(),
                instanceStateSet, null, 1);

        Assert.assertTrue(ret.getReservationSet().getItem().size() == 1);

        String token = ret.getNextToken();
        Assert.assertTrue(token != null);

        // expectedException.expect(BadEc2RequestException.class);
        Throwable e = null;
        try {
            Whitebox.invokeMethod(handler, "describeInstances", instanceIDs, instanceStateSet,
                    token, 0);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue(null != e);
        Assert.assertTrue(e instanceof BadEc2RequestException);
        Assert.assertTrue(e.getMessage().contains(
                "AWS Error Message: The parameter instancesSet cannot be used with the parameter nextToken"));

        e = null;
        try {
            Whitebox.invokeMethod(handler, "describeInstances", new TreeSet<String>(),
                    instanceStateSet,
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
            Whitebox.invokeMethod(handler, "describeInstances", instanceIDs, instanceStateSet, null,
                    100);
        } catch (Throwable ex) {
            e = ex;
        }
        Assert.assertTrue(null != e);
        Assert.assertTrue(e instanceof BadEc2RequestException);
        Assert.assertTrue(e.getMessage().contains(
                "AWS Error Message: The parameter instancesSet cannot be used with the parameter maxResults"));

        // get the second instance using next token
        ret = Whitebox.invokeMethod(handler, "describeInstances", new TreeSet<String>(),
                instanceStateSet, token, 0);
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

        handler.handle(null, null, null); // does nothing

        handler.handle(null, null, response); // no query params

        String responseString = sw.toString();
        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_PARAM_IN_QUERY));

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        handler.handle(new HashMap<String, String[]>(), null, response); // no query params

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

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_VERSION_IN_QUERY));

        // more than two version values here
        queryParams.put(VERSION_KEY, new String[] { VERSION_1, "version2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, null, response);

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

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_ACTION_IN_QUERY));

        // more than two action values here
        queryParams.put(ACTION_KEY, new String[] { "action1", "action2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, null, response);

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

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains("NotImplementedAction"));

    }
    
    @Test
    public void Test_handleBadQueryRequest() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, null);

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains("InvalidQuery"));

    }

    @Test
    public void Test_handleDescribeInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeInstancesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeInstances" });
        queryParams.put("NextToken", new String[]{});
        queryParams.put("MaxResults", new String[] { "50" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleDescribeImages() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeImagesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeImages" });
        queryParams.put("ImageId.1", new String[] { "ami-12345678" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleRunInstances() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("RunInstancesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "RunInstances" });
        queryParams.put("ImageId", new String[] { "img-1" });
        queryParams.put("MinCount", new String[] { "2" });
        queryParams.put("MaxCount", new String[] { "5" });
        queryParams.put("InstanceType", new String[] { "m1.small" });
        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("StartInstancesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "StartInstances" });

        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("StopInstancesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "StopInstances" });

        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("TerminateInstancesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "TerminateInstances" });

        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVpcsResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVpcs" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeVpcs_WithOtherRegion() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVpcsResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVpcs" });
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("region", "us-east-2");
        handler.handle(queryParams, headers, response);

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
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeSecurityGroupsResponse"),
                        Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeSecurityGroups" });

        handler.handle(queryParams, null, response);

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
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeInternetGatewaysResponse"),
                        Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeInternetGateways" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeInternetGateways_WithOtherRegion() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeInternetGatewaysResponse"),
                        Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeInternetGateways" });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("region", "us-east-2");
        handler.handle(queryParams, headers, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeTags() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(
                JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeTagsResponse"),
                        Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeTags" });

        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeRouteTablesResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeRouteTables" });

        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeSubnetsResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeSubnets" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeSubnets_WithOtherRegion() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeSubnetsResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeSubnets" });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("region", "us-east-2");
        handler.handle(queryParams, headers, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    @Test
    public void Test_handleCreateSubnets() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateSubnetResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateSubnet" });
        queryParams.put("CidrBlock", new String[] { "CidrBlock" });
        queryParams.put("VpcId", new String[] { "VpcId" });
        
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteSubnets() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteSubnetResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteSubnet" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateTags() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateTagsResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateTags" });
        queryParams.put("ResourceId.1", new String[] {"resource1"});
        queryParams.put("ResourceId.2", new String[] {"resource1"});
        queryParams.put("Tag.1.Key", new String[] {"Tag1"});
        queryParams.put("Tag.2.Key", new String[] {"Tag2"});
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteTags() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteTagsResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteTags" });
        queryParams.put("ResourceId.1", new String[] {"resource1"});
        queryParams.put("ResourceId.2", new String[] {"resource1"});
        
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateVpc() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateVpcResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateVpc" });

        queryParams.put("CidrBlock", new String[] { "CidrBlock" });
        queryParams.put("InstanceTenancy", new String[] { "InstanceTenancy" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateVpcNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateVpcResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateVpc" });

        queryParams.put("CidrBlock", null);
        queryParams.put("InstanceTenancy", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteVpc() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteVpcResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteVpc" });
        queryParams.put("VpcId", new String[] { "VpcId" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteVpcNull() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteVpcResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteVpc" });
        queryParams.put("VpcId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateInternetGateway() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateInternetGatewayResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateInternetGateway" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleAttachInternetGateway() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AttachInternetGatewayResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AttachInternetGateway" });
        queryParams.put("InternetGatewayId", new String[] { "InternetGatewayId1" });
        queryParams.put("VpcId", new String[] { "VpcId1" });
        
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleAttachInternetGatewayWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AttachInternetGatewayResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AttachInternetGateway" });
        queryParams.put("InternetGatewayId", null);
        queryParams.put("VpcId", null);
        
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateRoute() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateRouteResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateRoute" });
        queryParams.put("RouteTableId", new String[] { "RouteTableId1" });
        queryParams.put("InternetGatewayId", new String[] { "InternetGatewayId1" });
        queryParams.put("DestinationCidrBlock", new String[] { "DestinationCidrBlock1" });
        
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateRouteWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateRouteResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateRoute" });
        //queryParams.put("RouteTableId", null);
        //queryParams.put("InternetGatewayId", null);
        //queryParams.put("DestinationCidrBlock", null);

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteInternetGateway() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteInternetGatewayResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteInternetGateway" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    public void Test_handleDeleteInternetGatewayWithNull() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteInternetGatewayResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteInternetGateway" });
        queryParams.put("InternetGatewayId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleCreateVolume() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateVolumeResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateVolume" });
        queryParams.put("SnapshotId", new String[] { "SnapshotId" });
        queryParams.put("VolumeType", new String[] { "VolumeType" });
        queryParams.put("Size", new String[] { "0" });
        queryParams.put("AvailabilityZone", new String[] { "us-east-1" });
        queryParams.put("Iops", new String[] { "23" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateVolumeWithEmptyValues() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateVolumeResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateVolume" });
        queryParams.put("SnapshotId", null);
        queryParams.put("VolumeType", null);
        queryParams.put("Size", null);
        queryParams.put("AvailabilityZone", null);
        queryParams.put("Iops", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleDeleteVolume() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteVolumeResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteVolume" });
        queryParams.put("VolumeId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateRouteTable() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateRouteTableResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateRouteTable" });
        queryParams.put("CidrBlock", new String[] { "CidrBlock" });
        queryParams.put("VpcId", new String[] { "VpcId" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateRouteTableWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateRouteTableResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateRouteTable" });
        queryParams.put("CidrBlock", null);
        queryParams.put("VpcId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateSecurityGroup() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateSecurityGroupResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateSecurityGroup" });
        queryParams.put("GroupName", new String[] { "sgName" });
        queryParams.put("GroupDescription", new String[] { "sgDesc" });
        queryParams.put("VpcId", new String[] { "VpcId" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleCreateSecurityGroupWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("CreateSecurityGroupResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "CreateSecurityGroup" });
        queryParams.put("GroupName", null);
        queryParams.put("GroupDescription", null);
        queryParams.put("VpcId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleAuthorizeSecurityGroupIngress() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AuthorizeSecurityGroupIngressResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AuthorizeSecurityGroupIngress" });
        queryParams.put("GroupId", new String[] { "GroupId" });
        queryParams.put("FromPort", new String[] { "22" });
        queryParams.put("ToPort", new String[] { "22" });
        queryParams.put("CidrIp", new String[] { "CidrIp" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleAuthorizeSecurityGroupIngressWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AuthorizeSecurityGroupIngressResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AuthorizeSecurityGroupIngress" });
        queryParams.put("GroupId", null);
        queryParams.put("FromPort", null);
        queryParams.put("ToPort", null);
        queryParams.put("CidrIp", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleAuthorizeSecurityGroupEgress() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AuthorizeSecurityGroupEgressResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AuthorizeSecurityGroupEgress" });
        queryParams.put("GroupId", new String[] { "GroupId" });
        queryParams.put("FromPort", new String[] { "22" });
        queryParams.put("ToPort", new String[] { "22" });
        queryParams.put("CidrIp", new String[] { "CidrIp" });
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
    
    @Test
    public void Test_handleAuthorizeSecurityGroupEgressWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("AuthorizeSecurityGroupEgressResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "AuthorizeSecurityGroupEgress" });
        queryParams.put("GroupId", null);
        queryParams.put("FromPort", null);
        queryParams.put("ToPort", null);
        queryParams.put("CidrIp", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteRouteTable() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteRouteTableResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteRouteTable" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteRouteTableWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteRouteTableResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteRouteTable" });
        queryParams.put("RouteTableId", null);
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    
    @Test
    public void Test_handleDeleteSecurityGroup() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteSecurityGroupResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteSecurityGroup" });

        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDeleteSecurityGroupWithNullParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DeleteSecurityGroupResponse"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DeleteSecurityGroup" });
        queryParams.put("SecurityGroupId", null);
        handler.handle(queryParams, null, response);

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
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVolumesResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVolumes" });
        queryParams.put("MaxResults", new String[] { "50"});
       
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleDescribeAvailabilityZones() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(),
                Mockito.eq("DescribeAvailabilityZonesResponseType"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeAvailabilityZones" });
       
        handler.handle(queryParams, null, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleSafeAPI_DescribeAvailabilityZones() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(),
                Mockito.eq("DescribeAvailabilityZonesResponseType"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("region", "us-east-1");

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeAvailabilityZones" });
       
        handler.handle(queryParams, headers, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleNonSafeAPI_DescribeVolumes() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtil.marshall(Mockito.any(), Mockito.eq("DescribeVolumesResponseType"),
                Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "DescribeVolumes" });
        queryParams.put("MaxResults", new String[] { "50"});
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("region", "us-east-2");
        handler.handle(queryParams, headers, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.contains("Response"));
    }
}
