package com.tlswe.awsmock.ec2.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.common.util.TemplateUtils;
import com.tlswe.awsmock.ec2.cxf_generated.AttachInternetGatewayResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.AttachmentSetItemResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.AttachmentSetResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.AvailabilityZoneItemType;
import com.tlswe.awsmock.ec2.cxf_generated.AvailabilityZoneSetType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateInternetGatewayResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateRouteResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateRouteTableResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateSubnetResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateTagsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateVolumeResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.CreateVpcResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteInternetGatewayResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteRouteTableResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteSubnetResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteTagsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteVolumeResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DeleteVpcResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeAvailabilityZonesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseItemType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeInternetGatewaysResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeRouteTablesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeSecurityGroupsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeSubnetsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeTagsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeVolumesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeVolumesSetItemResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeVolumesSetResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeVpcsResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.GroupItemType;
import com.tlswe.awsmock.ec2.cxf_generated.GroupSetType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateChangeSetType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateType;
import com.tlswe.awsmock.ec2.cxf_generated.InternetGatewayAttachmentSetType;
import com.tlswe.awsmock.ec2.cxf_generated.InternetGatewayAttachmentType;
import com.tlswe.awsmock.ec2.cxf_generated.InternetGatewaySetType;
import com.tlswe.awsmock.ec2.cxf_generated.InternetGatewayType;
import com.tlswe.awsmock.ec2.cxf_generated.IpPermissionSetType;
import com.tlswe.awsmock.ec2.cxf_generated.IpPermissionType;
import com.tlswe.awsmock.ec2.cxf_generated.PlacementResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.ReservationInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.ReservationSetType;
import com.tlswe.awsmock.ec2.cxf_generated.RouteSetType;
import com.tlswe.awsmock.ec2.cxf_generated.RouteTableAssociationSetType;
import com.tlswe.awsmock.ec2.cxf_generated.RouteTableSetType;
import com.tlswe.awsmock.ec2.cxf_generated.RouteTableType;
import com.tlswe.awsmock.ec2.cxf_generated.RunInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesItemType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesSetType;
import com.tlswe.awsmock.ec2.cxf_generated.SecurityGroupItemType;
import com.tlswe.awsmock.ec2.cxf_generated.SecurityGroupSetType;
import com.tlswe.awsmock.ec2.cxf_generated.StartInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.StopInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.SubnetSetType;
import com.tlswe.awsmock.ec2.cxf_generated.SubnetType;
import com.tlswe.awsmock.ec2.cxf_generated.TagSetItemType;
import com.tlswe.awsmock.ec2.cxf_generated.TagSetType;
import com.tlswe.awsmock.ec2.cxf_generated.TerminateInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.VpcSetType;
import com.tlswe.awsmock.ec2.cxf_generated.VpcType;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.MockInternetGateway;
import com.tlswe.awsmock.ec2.model.MockInternetGatewayAttachmentType;
import com.tlswe.awsmock.ec2.model.MockRouteTable;
import com.tlswe.awsmock.ec2.model.MockSubnet;
import com.tlswe.awsmock.ec2.model.MockTags;
import com.tlswe.awsmock.ec2.model.MockVolume;
import com.tlswe.awsmock.ec2.model.MockVpc;
import com.tlswe.awsmock.ec2.servlet.MockEc2EndpointServlet;
import com.tlswe.awsmock.ec2.util.JAXBUtil;

/**
 * Class that handlers requests of AWS Query API for managing mock ec2 instances. This class works between
 * {@link MockEc2Controller} and {@link MockEc2EndpointServlet}. <br>
 * All object of mock ec2 instances are of the same type which is defined as property of "ec2.instance.class" in
 * aws-mock.properties (or if not overridden, as the default value defined in aws-mock-default.properties).
 *
 * @see MockEc2Controller
 * @see MockEc2EndpointServlet
 *
 * @author xma
 *
 */
public final class MockEC2QueryHandler {

    /**
     * Singleton instance of MockEC2QueryHandler.
     */
    private static MockEC2QueryHandler singletonMockEC2QueryHandler = null;

    /**
     * Log writer for this class.
     */
    private final Logger log = LoggerFactory.getLogger(MockEC2QueryHandler.class);

    /**
     * Class for all mock ec2 instances, which should extend {@link AbstractMockEc2Instance}.
     */
    private static final String MOCK_EC2_INSTANCE_CLASS_NAME = PropertiesUtils
            .getProperty(Constants.PROP_NAME_EC2_INSTANCE_CLASS);

    /**
     * Default placement for this aws-mock, defined in aws-mock.properties (or if not overridden, as defined in
     * aws-mock-default.properties).
     */
    private static final PlacementResponseType DEFAULT_MOCK_PLACEMENT = new PlacementResponseType();

    /**
     * The xml template filename for error response body.
     */
    private static final String ERROR_RESPONSE_TEMPLATE = "error.xml.ftl";

    /**
     * Description for the link to AWS QUERY API reference.
     */
    private static final String REF_EC2_QUERY_API_DESC = "See http://docs.aws.amazon.com/AWSEC2/latest/UserGuide"
            + "/using-query-api.html for building a valid query.";

    /**
     * Predefined AMIs, as properties of predefined.mock.ami.X in aws-mock.properties (or if not overridden, as defined
     * in aws-mock-default.properties). We use {@link TreeSet} here so that those AMIs are loaded and displayed
     * (described) in the same order the are defined in the .properties file.
     */
    private static final Set<String> MOCK_AMIS = new TreeSet<String>();

    /**
     * Instance of {@link MockEc2Controller} used in this class that controls mock Ec2 Instances.
     */
    private final MockEc2Controller mockEc2Controller = MockEc2Controller.getInstance();

    /**
     * Instance of {@link MockVpcController} used in this class that controls mock Vpc Instances.
     */
    private final MockVpcController mockVpcController = MockVpcController.getInstance();

    /**
     * Instance of {@link MockSubnetController} used in this class that controls mock Subnet instances.
     */
    private final MockSubnetController mockSubnetController = MockSubnetController.getInstance();

    /**
     * Instance of {@link MockRouteTableController} used in this class that controls mock RouteTable instances.
     */
    private final MockRouteTableController mockRouteTableController = MockRouteTableController
            .getInstance();

    /**
     * Instance of {@link MockVolumeController} used in this class that controls mock Volume instances.
     */
    private final MockVolumeController mockVolumeController = MockVolumeController
            .getInstance();

    /**
     * Instance of {@link MockTagsController} used in this class that controls mock Tags instances.
     */
    private final MockTagsController mockTagsController = MockTagsController
            .getInstance();

    /**
     * Instance of {@link MockInternetGatewayControllerTest} used in this class that
     * controls mock Internet gateway instances.
     */
    private final MockInternetGatewayController mockInternetGatewayController = MockInternetGatewayController
            .getInstance();

    /**
     * Predefined mock vpc id.
     */
    private static final String MOCK_VPC_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_VPC_ID);

    /**
     * Predefined mock vpc state.
     */
    private static final String MOCK_VPC_STATE = PropertiesUtils
            .getProperty(Constants.PROP_NAME_VPC_STATE);

    /**
     * Predefined mock private ip address.
     */
    private static final String MOCK_PRIVATE_IP_ADDRESS = PropertiesUtils
            .getProperty(Constants.PROP_NAME_PRIVATE_IP_ADDRESS);

    /**
     * Predefined mock subnet id.
     */
    private static final String MOCK_SUBNET_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SUBNET_ID);

    /**
     * Predefined mock security group id.
     */
    private static final String MOCK_SECURITY_GROUP_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_GROUP_ID);

    /**
     * Predefined mock security owner id.
     */
    private static final String MOCK_SECURITY_OWNER_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_OWNER_ID);

    /**
     * Predefined mock security group name.
     */
    private static final String MOCK_SECURITY_GROUP_NAME = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_GROUP_NAME);

    /**
     * Predefined mock ip protocol.
     */
    private static final String MOCK_IP_PROTOCOL = PropertiesUtils
            .getProperty(Constants.PROP_NAME_IP_PROTOCOL);

    /**
     * Predefined mock cidr block.
     */
    private static final String MOCK_CIDR_BLOCK = PropertiesUtils
            .getProperty(Constants.PROP_NAME_CIDR_BLOCK);

    /**
     * Predefined mock source ip port.
     */
    private static final int MOCK_SOURCE_PORT = PropertiesUtils
            .getIntFromProperty(Constants.PROP_NAME_SOURCE_PORT);

    /**
     * Predefined mock destination ip port.
     */
    private static final int MOCK_DEST_PORT = PropertiesUtils
            .getIntFromProperty(Constants.PROP_NAME_DEST_PORT);

    /**
     * Predefined mock volume Id.
     */
    private static final String MOCK_VOLUME_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_VOLUME_ID);

    /**
     * Predefined mock instance Id.
     */
    private static final String MOCK_INSTANCE_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_INSTANCE_ID);

    /**
     * Predefined mock volume Type.
     */
    private static final String MOCK_VOLUME_TYPE = PropertiesUtils
            .getProperty(Constants.PROP_NAME_VOLUME_TYPE);

    /**
     * Predefined mock volume Status.
     */
    private static final String MOCK_VOLUME_STATUS = PropertiesUtils
            .getProperty(Constants.PROP_NAME_VOLUME_STATUS);

    /**
     * The remaining paged records of instance IDs per token by 'describeInstances'.
     */
    private static Map<String, Set<String>> token2RemainingDescribedInstanceIDs =
            new ConcurrentHashMap<String, Set<String>>();

    /**
     * The remaining paged records of instance IDs per token by 'describeVolumes'.
     */
    private static Map<String, Set<String>> token2RemainingDescribedVolumeIDs =
            new ConcurrentHashMap<String, Set<String>>();

    /**
    /**
     * A common random generator.
     */
    private static Random random = new Random();

    /**
     * The chars used to generate tokens (those tokens in describeInstances req/resp pagination).
     */
    private static final String TOKEN_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Token prefix length.
     *
     * AWS's token is 276 bytes in length (19 fixed + 240 generated per response + 17 fixed), we just mock that way.
     */
    protected static final int TOKEN_PREFIX_LEN = 19;

    /**
     * Token suffix length.
     */
    protected static final int TOKEN_SUFFIX_LEN = 17;

    /**
     * Token mid-string length.
     */
    protected static final int TOKEN_MIDDLE_LEN = 240;

    /**
     * The prefix string, which would be determined on app startup.
     */
    protected static final String TOKEN_PREFIX;

    /**
     * The suffix string, which would be determined on app startup.
     */
    protected static final String TOKEN_SUFFIX;

    /**
     * Default page size for pagination in describeInstance response.
     */
    protected static final int MAX_RESULTS_DEFAULT = 1000;

    static {
        DEFAULT_MOCK_PLACEMENT.setAvailabilityZone(
                PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_PLACEMENT));
        MOCK_AMIS.addAll(PropertiesUtils.getPropertiesByPrefix("predefined.mock.ami."));

        /*
         * We determine the token's prefix and suffix at the start of webapp and don't change them.
         */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKEN_PREFIX_LEN; i++) {
            sb.append(TOKEN_DICT.charAt(random.nextInt(TOKEN_DICT.length())));
        }
        TOKEN_PREFIX = sb.toString();

        sb = new StringBuilder();
        for (int i = 0; i < TOKEN_SUFFIX_LEN; i++) {
            sb.append(TOKEN_DICT.charAt(random.nextInt(TOKEN_DICT.length())));
        }
        TOKEN_SUFFIX = sb.toString();
    }

    /**
     * Constructor of MockEC2QueryHandler is made private and only called once by {@link #getInstance()}.
     */
    private MockEC2QueryHandler() {
    }

    /**
     *
     * @return singleton instance of {@link MockEC2QueryHandler}
     */
    public static MockEC2QueryHandler getInstance() {
        if (null == singletonMockEC2QueryHandler) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockEc2Controller.class) {
                if (null == singletonMockEC2QueryHandler) {
                    singletonMockEC2QueryHandler = new MockEC2QueryHandler();
                }
            }
        }
        return singletonMockEC2QueryHandler;
    }

    /**
     * Hub method for parsing query prarmeters and generate and write xml response.
     *
     * @param queryParams
     *            map of query parameters from http request, which is from standard AWS Query API
     * @param response
     *            http servlet response to handle with
     * @throws IOException
     *             in case of failure of getting response's writer
     *
     */
    public void handle(final Map<String, String[]> queryParams, final HttpServletResponse response)
            throws IOException {
        if (null == response) {
            // do nothing in case null is passed in
            return;
        }
        String responseXml = null;
        if (null == queryParams || queryParams.size() == 0) {
            // no params found at all - write an error xml response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseXml = getXmlError("InvalidQuery",
                    "No parameter in query at all! " + REF_EC2_QUERY_API_DESC);
        } else {
            // parse the parameters in query
            String[] versionParamValues = queryParams.get("Version");

            if (null == versionParamValues || versionParamValues.length != 1) {
                // no version param found - write an error xml response
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseXml = getXmlError("InvalidQuery",
                        "There should be a parameter of 'Version' provided in the query! "
                                + REF_EC2_QUERY_API_DESC);
            } else {

                String version = versionParamValues[0];

                String[] actions = queryParams.get("Action");

                if (null == actions || actions.length != 1) {
                    // no action found - write response for error
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseXml = getXmlError("InvalidQuery",
                            "There should be a parameter of 'Action' provided in the query! "
                                    + REF_EC2_QUERY_API_DESC);
                } else {

                    String action = actions[0];

                    try {

                        response.setStatus(HttpServletResponse.SC_OK);

                        if ("RunInstances".equals(action)) {

                            String imageID = queryParams.get("ImageId")[0];
                            String instanceType = queryParams.get("InstanceType")[0];
                            int minCount = Integer.parseInt(queryParams.get("MinCount")[0]);
                            int maxCount = Integer.parseInt(queryParams.get("MaxCount")[0]);

                            responseXml = JAXBUtil.marshall(
                                    runInstances(imageID, instanceType, minCount, maxCount),
                                    "RunInstancesResponse", version);

                        } else if ("DescribeImages".equals(action)) {
                            responseXml = JAXBUtil.marshall(describeImages(),
                                    "DescribeImagesResponse", version);
                        } else {

                            // the following interface calls need instanceIDs
                            // provided
                            // in params, we put all the instanceIDs into a set
                            // for
                            // usage
                            Set<String> instanceIDs = parseInstanceIDs(queryParams);

                            if ("DescribeInstances".equals(action)) {

                                Set<String> instanceStates = parseInstanceStates(queryParams);

                                String[] paramNextToken = queryParams.get("NextToken");
                                String nextToken = null == paramNextToken
                                        || paramNextToken.length == 0 ? null
                                                : paramNextToken[0];
                                String[] paramMaxResults = queryParams.get("MaxResults");
                                int maxResults = null == paramMaxResults
                                        || paramMaxResults.length == 0 ? 0
                                                : NumberUtils.toInt(paramMaxResults[0]);

                                responseXml = JAXBUtil.marshall(
                                        describeInstances(instanceIDs, instanceStates, nextToken,
                                                maxResults),
                                        "DescribeInstancesResponse", version);

                            } else if ("StartInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(startInstances(instanceIDs),
                                        "StartInstancesResponse",
                                        version);

                            } else if ("StopInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(stopInstances(instanceIDs),
                                        "StopInstancesResponse",
                                        version);

                            } else if ("TerminateInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(terminateInstances(instanceIDs),
                                        "TerminateInstancesResponse", version);

                            } else if ("DescribeVpcs".equals(action)) {

                                responseXml = JAXBUtil.marshall(describeVpcs(),
                                        "DescribeVpcsResponse", version);

                            } else if ("CreateVpc".equals(action)) {

                                String[] cidrBlockParam = queryParams.get("CidrBlock");
                                String cidrBlock = null == cidrBlockParam
                                        || cidrBlockParam.length == 0 ? null
                                                : cidrBlockParam[0];

                                String[] instanceTenancyParam = queryParams.get("InstanceTenancy");
                                String instanceTenancy = null == instanceTenancyParam
                                        || instanceTenancyParam.length == 0 ? null
                                                : instanceTenancyParam[0];

                                responseXml = JAXBUtil.marshall(
                                        createVpc(cidrBlock, instanceTenancy),
                                        "CreateVpcResponse", version);
                            } else if ("DeleteVpc".equals(action)) {

                                String[] vpcIdParam = queryParams.get("VpcId");
                                String vpcId = null == vpcIdParam
                                        || vpcIdParam.length == 0 ? null
                                                : vpcIdParam[0];
                                responseXml = JAXBUtil.marshall(deleteVpc(vpcId),
                                        "DeleteVpcResponse", version);
                            } else if ("CreateRouteTable".equals(action)) {

                                String[] cidrBlockParam = queryParams.get("CidrBlock");
                                String cidrBlock = null == cidrBlockParam
                                        || cidrBlockParam.length == 0 ? null
                                                : cidrBlockParam[0];

                                String[] vpcIdParam = queryParams.get("VpcId");
                                String vpcId = null == vpcIdParam
                                        || vpcIdParam.length == 0 ? null
                                                : vpcIdParam[0];

                                responseXml = JAXBUtil.marshall(createRouteTable(cidrBlock, vpcId),
                                        "CreateRouteTableResponse", version);
                            } else if ("CreateRoute".equals(action)) {

                                String[] cidrBlockParam = queryParams.get("DestinationCidrBlock");
                                String cidrBlock = null == cidrBlockParam
                                        || cidrBlockParam.length == 0 ? null
                                                : cidrBlockParam[0];
                                String[] routeTableIdParam = queryParams.get("RouteTableId");
                                String routeTableId = null == routeTableIdParam
                                        || routeTableIdParam.length == 0 ? null
                                                : routeTableIdParam[0];

                                String[] internetGatewayIdParam = queryParams
                                        .get("InternetGatewayId");
                                String internetGatewayId = null == internetGatewayIdParam
                                        || internetGatewayIdParam.length == 0 ? null
                                                : internetGatewayIdParam[0];

                                responseXml = JAXBUtil.marshall(
                                        createRoute(cidrBlock, internetGatewayId, routeTableId),
                                        "CreateRouteResponse", version);
                            } else if ("DeleteRouteTable".equals(action)) {

                                String[] routeTableIdParam = queryParams.get("RouteTableId");
                                String routeTableId = null == routeTableIdParam
                                        || routeTableIdParam.length == 0 ? null
                                                : routeTableIdParam[0];

                                responseXml = JAXBUtil.marshall(deleteRouteTable(routeTableId),
                                        "DeleteRouteTableResponse", version);

                            } else if ("DescribeSecurityGroups".equals(action)) {
                                responseXml = JAXBUtil.marshall(describeSecurityGroups(),
                                        "DescribeSecurityGroupsResponse", version);

                            } else if ("DescribeInternetGateways".equals(action)) {

                                responseXml = JAXBUtil.marshall(describeInternetGateways(),
                                        "DescribeInternetGatewaysResponse", version);
                            } else if ("CreateInternetGateway".equals(action)) {

                                responseXml = JAXBUtil.marshall(createInternetGateway(),
                                        "CreateInternetGatewayResponse", version);
                            } else if ("AttachInternetGateway".equals(action)) {

                                String[] internetGatewayIdParam = queryParams
                                        .get("InternetGatewayId");
                                String internetGatewayId = null == internetGatewayIdParam
                                        || internetGatewayIdParam.length == 0 ? null
                                                : internetGatewayIdParam[0];

                                String[] vpcIdParam = queryParams.get("VpcId");
                                String vpcId = null == vpcIdParam
                                        || vpcIdParam.length == 0 ? null
                                                : vpcIdParam[0];

                                responseXml = JAXBUtil.marshall(
                                        attachInternetGateway(internetGatewayId, vpcId),
                                        "AttachInternetGatewayResponse", version);
                            } else if ("DeleteInternetGateway".equals(action)) {

                                String[] internetGatewayIdParam = queryParams
                                        .get("InternetGatewayId");
                                String internetGatewayId = null == internetGatewayIdParam
                                        || internetGatewayIdParam.length == 0 ? null
                                                : internetGatewayIdParam[0];

                                responseXml = JAXBUtil.marshall(
                                        deleteInternetGateway(internetGatewayId),
                                        "DeleteInternetGatewayResponse", version);
                            } else if ("DescribeRouteTables".equals(action)) {

                                responseXml = JAXBUtil.marshall(describeRouteTables(),
                                        "DescribeRouteTablesResponse", version);
                            } else if ("DescribeVolumes".equals(action)) {

                                String[] paramNextToken = queryParams.get("NextToken");
                                String nextToken = null == paramNextToken
                                         || paramNextToken.length == 0 ? null
                                                 : paramNextToken[0];
                                 String[] paramMaxResults = queryParams.get("MaxResults");
                                 int maxResults = null == paramMaxResults
                                         || paramMaxResults.length == 0 ? 0
                                                 : NumberUtils.toInt(paramMaxResults[0]);

                                responseXml = JAXBUtil.marshall(describeVolumes(nextToken, maxResults),
                                         "DescribeVolumesResponseType", version);

                            } else if ("CreateVolume".equals(action)) {

                                String[] snapshotIdParam = queryParams.get("SnapshotId");
                                String snapshotId = null == snapshotIdParam
                                        || snapshotIdParam.length == 0 ? null
                                                : snapshotIdParam[0];

                                String[] volumeTypeParam = queryParams.get("VolumeType");
                                String volumeType = null == volumeTypeParam
                                        || volumeTypeParam.length == 0 ? null
                                                : volumeTypeParam[0];
                                String[] sizeParam = queryParams.get("Size");
                                String size = null == sizeParam
                                        || sizeParam.length == 0 ? null
                                                : sizeParam[0];
                                String[] availabilityZoneParam = queryParams
                                        .get("AvailabilityZone");
                                String availabilityZone = null == availabilityZoneParam
                                        || availabilityZoneParam.length == 0 ? null
                                                : availabilityZoneParam[0];
                                String[] iopsParam = queryParams.get("Iops");
                                String iops = null == iopsParam
                                        || iopsParam.length == 0 ? null
                                                : iopsParam[0];
                                int iopsValue = 0;
                                if (iops != null) {
                                    iopsValue = Integer.parseInt(iops);
                                }

                                responseXml = JAXBUtil.marshall(createVolume(volumeType, size,
                                        availabilityZone, iopsValue, snapshotId),
                                        "CreateVolumeResponse", version);
                            } else if ("DeleteVolume".equals(action)) {

                                String[] volumeIdParam = queryParams.get("VolumeId");
                                String volumeId = null == volumeIdParam
                                        || volumeIdParam.length == 0 ? null
                                                : volumeIdParam[0];

                                responseXml = JAXBUtil.marshall(deleteVolume(volumeId),
                                        "DeleteVolumeResponse", version);
                            } else if ("DescribeSubnets".equals(action)) {
                                responseXml = JAXBUtil.marshall(describeSubnets(),
                                        "DescribeSubnetsResponseType", version);
                            } else if ("CreateSubnet".equals(action)) {

                                String[] cidrBlockParam = queryParams.get("CidrBlock");
                                String cidrBlock = null == cidrBlockParam
                                        || cidrBlockParam.length == 0 ? null
                                                : cidrBlockParam[0];

                                String[] vpcIdParam = queryParams.get("VpcId");
                                String vpcId = null == vpcIdParam
                                        || vpcIdParam.length == 0 ? null
                                                : vpcIdParam[0];

                                responseXml = JAXBUtil.marshall(createSubnet(vpcId, cidrBlock),
                                        "CreateSubnetResponse", version);
                            } else if ("CreateTags".equals(action)) {

                                int tagsCounter = 1;
                                List<String> resources = new ArrayList<String>();
                                Map<String, String> tags = new HashMap<String, String>();

                                while (true) {
                                    if (queryParams.containsKey("ResourceId." + tagsCounter)) {
                                        String[] resourceIdParam = queryParams
                                                .get("ResourceId." + tagsCounter);
                                        String resourceId = null == resourceIdParam
                                                || resourceIdParam.length == 0 ? null
                                                        : resourceIdParam[0];
                                        resources.add(resourceId);
                                        tagsCounter++;
                                    } else {

                                        break;
                                    }
                                }

                                tagsCounter = 1;
                                while (true) {
                                    if (queryParams.containsKey("Tag." + tagsCounter + ".Key")) {
                                        String[] tagKeyParam = queryParams
                                                .get("Tag." + tagsCounter + ".Key");
                                        String tagKey = null == tagKeyParam
                                                || tagKeyParam.length == 0 ? null
                                                        : tagKeyParam[0];
                                        String[] tagValueParam = queryParams
                                                .get("Tag." + tagsCounter + ".Value");
                                        String tagValue = null == tagValueParam
                                                || tagValueParam.length == 0 ? null
                                                        : tagValueParam[0];

                                        tags.put(tagKey, tagValue);

                                        tagsCounter++;
                                    } else {

                                        break;
                                    }
                                }
                                responseXml = JAXBUtil.marshall(createTags(resources, tags),
                                        "CreateTagsResponse", version);
                            } else if ("DeleteTags".equals(action)) {

                                int tagsCounter = 1;
                                List<String> resources = new ArrayList<String>();
                                while (true) {
                                    if (queryParams.containsKey("ResourceId." + tagsCounter)) {
                                        String[] resourceIdParam = queryParams
                                                .get("ResourceId." + tagsCounter);
                                        String resourceId = null == resourceIdParam
                                                || resourceIdParam.length == 0 ? null
                                                        : resourceIdParam[0];
                                        resources.add(resourceId);
                                        tagsCounter++;
                                    } else {

                                        break;
                                    }
                                }

                                responseXml = JAXBUtil.marshall(deleteTags(resources),
                                        "DeleteTagsResponse", version);
                            } else if ("DeleteSubnet".equals(action)) {

                                String[] subnetIdParam = queryParams.get("SubnetId");
                                String subnetId = null == subnetIdParam
                                        || subnetIdParam.length == 0 ? null
                                                : subnetIdParam[0];

                                responseXml = JAXBUtil.marshall(deleteSubnet(subnetId),
                                        "DeleteSubnetResponse", version);
                            } else if ("DescribeAvailabilityZones".equals(action)) {
                                responseXml = JAXBUtil.marshall(describeAvailabilityZones(),
                                        "DescribeAvailabilityZonesResponseType", version);
                            } else if ("DescribeTags".equals(action)) {
                                responseXml = JAXBUtil.marshall(describeTags(),
                                        "DescribeTagsResponse", version);
                            } else {
                                // unsupported/unimplemented action - write an
                                // error
                                // response
                                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                                String allImplementedActions = "runInstances|stopInstances|startInstances|"
                                        + "terminateInstances|describeInstances|describeImages";
                                responseXml = getXmlError("NotImplementedAction",
                                        "Action '" + action
                                                + "' has not been implemented yet in aws-mock. "
                                                + "For now we only support actions as following: "
                                                + allImplementedActions);
                            }
                        }

                    } catch (BadEc2RequestException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseXml = getXmlError("InvalidQuery",
                                "invalid request for '" + action + "'. " + e.getMessage()
                                        + REF_EC2_QUERY_API_DESC);
                    } catch (AwsMockException e) {
                        log.error("server error occured while processing '{}' request. {}", action,
                                e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        responseXml = getXmlError("InternalError", e.getMessage());
                    }

                }
            }
        }
        response.getWriter().write(responseXml);
        response.getWriter().flush();
    }

    /**
     * Parse instance states from query parameters.
     *
     * @param queryParams
     *            map of query parameters in http request
     * @return a set of instance states in the parameter map
     */
    private Set<String> parseInstanceStates(final Map<String, String[]> queryParams) {
        Set<String> instanceStates = new TreeSet<String>();

        for (String queryKey : queryParams.keySet()) {
            // e.g. Filter.1.Value.1: running, Filter.1.Value.2: pending
            if (queryKey.startsWith("Filter.1.Value")) {
                for (String state : queryParams.get(queryKey)) {
                    instanceStates.add(state);
                }
            }
        }
        return instanceStates;
    }

    /**
     * Handles "describeAvailabilityZones" request, as simple as without any filters to use.
     *
     * @return a DescribeAvailabilityZonesResponseType with our predefined AMIs
     * in aws-mock.properties (or if not overridden, as defined in aws-mock-default.properties)
     */
    private DescribeAvailabilityZonesResponseType describeAvailabilityZones() {
        DescribeAvailabilityZonesResponseType ret = new DescribeAvailabilityZonesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        AvailabilityZoneSetType info = new AvailabilityZoneSetType();

        AvailabilityZoneItemType item = new AvailabilityZoneItemType();
        item.setRegionName(DEFAULT_MOCK_PLACEMENT.getAvailabilityZone());
        item.setZoneName(DEFAULT_MOCK_PLACEMENT.getAvailabilityZone());

        info.getItem().add(item);
        ret.setAvailabilityZoneInfo(info);
        return ret;
    }

    /**
     * Parse instance IDs from query parameters.
     *
     * @param queryParams
     *            map of query parameters in http request
     * @return a set of instance IDs in the parameter map
     */
    private Set<String> parseInstanceIDs(final Map<String, String[]> queryParams) {
        Set<String> ret = new TreeSet<String>();

        Set<Map.Entry<String, String[]>> entries = queryParams.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            if (null != entry && null != entry.getKey()
                    && entry.getKey().matches("InstanceId\\.(\\d)+")) {
                if (null != entry.getValue() && entry.getValue().length > 0) {
                    ret.add(entry.getValue()[0]);
                }
            }
        }
        return ret;
    }

    /**
     * Handles "describeInstances" request, with filters of instanceIDs and instanceStates, and returns response with
     * all mock ec2 instances if no instance IDs specified.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to describe
     * @param instanceStates
     *            a filter of specified instance states for the target instance to describe
     * @param token
     *            token for next page
     * @param pMaxResults
     *            max result in page, if over 1000, only 1000 instances would be returned
     *
     * @return a DescribeInstancesResponse with information for all mock ec2 instances to describe
     */
    private DescribeInstancesResponseType describeInstances(final Set<String> instanceIDs,
            final Set<String> instanceStates, final String token, final int pMaxResults) {

        Set<String> idsInThisPageIfToken = null;
        if (null != token && token.length() > 0) {
            if (null != instanceIDs && instanceIDs.size() > 0) {
                throw new BadEc2RequestException(
                        "DescribeInstances",
                        "AWS Error Code: InvalidParameterCombination, AWS Error Message: The parameter instancesSet "
                                + "cannot be used with the parameter nextToken");
            }
            // should retrieve next page using token
            idsInThisPageIfToken = token2RemainingDescribedInstanceIDs.get(token);
            if (null == idsInThisPageIfToken) {
                // mock real AWS' 400 error message in case of invalid token
                throw new BadEc2RequestException("DescribeInstances",
                        "AWS Error Code: InvalidParameterValue, AWS Error Message: Unable to parse pagination token");
            }
        }

        /**
         * The calculated maxResults used in pagination.
         */
        int maxResults = pMaxResults;

        if (maxResults > 0) {
            if (null != instanceIDs && instanceIDs.size() > 0) {
                throw new BadEc2RequestException(
                        "DescribeInstances",
                        "AWS Error Code: InvalidParameterCombination, AWS Error Message: The parameter instancesSet "
                                + "cannot be used with the parameter maxResults");
            }
        } else {
            maxResults = MAX_RESULTS_DEFAULT;
        }

        DescribeInstancesResponseType ret = new DescribeInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        ReservationSetType resSet = new ReservationSetType();

        mockEc2Controller.getAllMockEc2Instances();

        List<String> idsToDescribe = null;

        if (null != token && token.length() > 0) {
            idsToDescribe = new ArrayList<String>(
                    token2RemainingDescribedInstanceIDs.remove(token));
        } else {
            // will return all instance IDs if the param 'instanceIDs' is empty here
            idsToDescribe = mockEc2Controller.listInstanceIDs(instanceIDs);
        }

        if (idsToDescribe.size() > maxResults) {
            // generate next token (for next page of results) and put the remaining IDs to the map for later use
            String newToken = generateToken();
            // deduct the current page instances from the total remaining and put the rest into map again, with new
            // token as key
            token2RemainingDescribedInstanceIDs.put(newToken,
                    new TreeSet<String>(idsToDescribe.subList(maxResults, idsToDescribe.size())));
            // set idsToDescribe as the top maxResults instance IDs
            idsToDescribe = new ArrayList<String>(idsToDescribe.subList(0, maxResults));
            // put the new token into response
            ret.setNextToken(newToken);
        }

        List<String> invalidInstanceIDs = new ArrayList<String>();

        for (String id : idsToDescribe) {
            AbstractMockEc2Instance instance = mockEc2Controller.getMockEc2Instance(id);
            if (null == instance) {
                invalidInstanceIDs.add(id);
            } else {
                String instanceState = instance.getInstanceState().getName();
                // get instances with specified states
                if (!instanceStates.isEmpty() && !instanceStates.contains(instanceState)) {
                    continue;
                }

                ReservationInfoType resInfo = new ReservationInfoType();
                resInfo.setReservationId(UUID.randomUUID().toString());
                resInfo.setOwnerId("mock-owner");

                GroupSetType groupSet = new GroupSetType();
                GroupItemType gItem = new GroupItemType();
                gItem.setGroupId("default");
                groupSet.getItem().add(gItem);
                resInfo.setGroupSet(groupSet);

                RunningInstancesSetType instsSet = new RunningInstancesSetType();

                RunningInstancesItemType instItem = new RunningInstancesItemType();
                instItem.setInstanceId(instance.getInstanceID());

                instItem.setPlacement(DEFAULT_MOCK_PLACEMENT);

                InstanceStateType st = new InstanceStateType();
                st.setCode(instance.getInstanceState().getCode());
                st.setName(instance.getInstanceState().getName());

                instItem.setInstanceState(st);
                instItem.setImageId(instance.getImageId());
                instItem.setInstanceType(instance.getInstanceType().getName());
                instItem.setDnsName(instance.getPubDns());

                // set network information
                instItem.setVpcId(MOCK_VPC_ID);
                instItem.setPrivateIpAddress(MOCK_PRIVATE_IP_ADDRESS);
                instItem.setSubnetId(MOCK_SUBNET_ID);

                instsSet.getItem().add(instItem);

                resInfo.setInstancesSet(instsSet);

                resSet.getItem().add(resInfo);

            }

        }

        if (invalidInstanceIDs.size() > 0) {
            throw new BadEc2RequestException(
                    "DescribeInstances",
                    "AWS Error Code: InvalidInstanceID.NotFound, AWS Error Message: The instance IDs '"
                            + StringUtils.join(invalidInstanceIDs, ", ") + "' do not exist");
        }

        ret.setReservationSet(resSet);

        return ret;

    }

    /**
     * Generate a new token used in describeInstanceResponse while paging enabled.
     *
     * @return a random string as a token
     */
    protected String generateToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKEN_MIDDLE_LEN; i++) {
            sb.append(TOKEN_DICT.charAt(random.nextInt(TOKEN_DICT.length())));
        }
        return TOKEN_PREFIX + sb.toString() + TOKEN_SUFFIX;
    }

    /**
     * Handles "runInstances" request, with only simplified filters of imageId, instanceType, minCount and maxCount.
     *
     * @param imageId
     *            AMI of new mock ec2 instance(s)
     * @param instanceType
     *            type(scale) of new mock ec2 instance(s), refer to {@link AbstractMockEc2Instance#instanceType}
     * @param minCount
     *            max count of instances to run
     * @param maxCount
     *            min count of instances to run
     * @return a RunInstancesResponse that includes all information for the started new mock ec2 instances
     */
    private RunInstancesResponseType runInstances(final String imageId, final String instanceType,
            final int minCount, final int maxCount) {

        RunInstancesResponseType ret = new RunInstancesResponseType();

        RunningInstancesSetType instSet = new RunningInstancesSetType();

        Class<? extends AbstractMockEc2Instance> clazzOfMockEc2Instance = null;

        try {
            // clazzOfMockEc2Instance = (Class<? extends MockEc2Instance>) Class.forName(MOCK_EC2_INSTANCE_CLASS_NAME);

            clazzOfMockEc2Instance = (Class.forName(MOCK_EC2_INSTANCE_CLASS_NAME)
                    .asSubclass(AbstractMockEc2Instance.class));

        } catch (ClassNotFoundException e) {
            throw new AwsMockException(
                    "badly configured class '" + MOCK_EC2_INSTANCE_CLASS_NAME + "' not found", e);
        }

        List<AbstractMockEc2Instance> newInstances = null;

        newInstances = mockEc2Controller
                .runInstances(clazzOfMockEc2Instance, imageId, instanceType, minCount, maxCount);

        for (AbstractMockEc2Instance i : newInstances) {
            RunningInstancesItemType instItem = new RunningInstancesItemType();
            instItem.setInstanceId(i.getInstanceID());
            instItem.setImageId(i.getImageId());
            instItem.setInstanceType(i.getInstanceType().getName());
            InstanceStateType state = new InstanceStateType();
            state.setCode(i.getInstanceState().getCode());
            state.setName(i.getInstanceState().getName());
            instItem.setInstanceState(state);
            instItem.setDnsName(i.getPubDns());
            instItem.setPlacement(DEFAULT_MOCK_PLACEMENT);

            // set network information
            instItem.setVpcId(MOCK_VPC_ID);
            instItem.setPrivateIpAddress(MOCK_PRIVATE_IP_ADDRESS);
            instItem.setSubnetId(MOCK_SUBNET_ID);

            instSet.getItem().add(instItem);

        }

        ret.setInstancesSet(instSet);

        return ret;

    }

    /**
     * Handles "startInstances" request, with only a simplified filter of instanceIDs.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to start
     * @return a StartInstancesResponse with information for all mock ec2 instances to start
     */
    private StartInstancesResponseType startInstances(final Set<String> instanceIDs) {
        StartInstancesResponseType ret = new StartInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(mockEc2Controller.startInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;

    }

    /**
     * Handles "stopInstances" request, with only a simplified filter of instanceIDs.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to stop
     * @return a StopInstancesResponse with information for all mock ec2 instances to stop
     */
    private StopInstancesResponseType stopInstances(final Set<String> instanceIDs) {
        StopInstancesResponseType ret = new StopInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(mockEc2Controller.stopInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    /**
     * Handles "terminateInstances" request, with only a simplified filter of instanceIDs.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to terminate
     * @return a StartInstancesResponse with information for all mock ec2 instances to terminate
     */
    private TerminateInstancesResponseType terminateInstances(final Set<String> instanceIDs) {
        TerminateInstancesResponseType ret = new TerminateInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(mockEc2Controller.terminateInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    /**
     * Handles "describeImages" request, as simple as without any filters to use.
     *
     * @return a DescribeImagesResponse with our predefined AMIs in aws-mock.properties (or if not overridden, as
     *         defined in aws-mock-default.properties)
     */
    private DescribeImagesResponseType describeImages() {
        DescribeImagesResponseType ret = new DescribeImagesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        DescribeImagesResponseInfoType info = new DescribeImagesResponseInfoType();
        for (String ami : MOCK_AMIS) {
            DescribeImagesResponseItemType item = new DescribeImagesResponseItemType();
            item.setImageId(ami);
            info.getItem().add(item);
        }
        ret.setImagesSet(info);

        return ret;
    }

    /**
     * Handles "describeRouteTables" request and returns response with a route table.
     *
     * @return a DescribeRouteTablesResponseType with our predefined route table in aws-mock.properties (or if not
     *         overridden, as defined in aws-mock-default.properties)
     */
    private DescribeRouteTablesResponseType describeRouteTables() {
        DescribeRouteTablesResponseType ret = new DescribeRouteTablesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        RouteTableSetType routeTableSet = new RouteTableSetType();
        for (Iterator<MockRouteTable> mockRouteTable = mockRouteTableController
                .describeRouteTables().iterator(); mockRouteTable.hasNext();) {
            MockRouteTable item = mockRouteTable.next();

            RouteTableType routeTable = new RouteTableType();
            routeTable.setVpcId(item.getVpcId());
            routeTable.setRouteTableId(item.getRouteTableId());

            RouteTableAssociationSetType associationSet = new RouteTableAssociationSetType();
            routeTable.setAssociationSet(associationSet);

            RouteSetType routeSet = new RouteSetType();
            routeTable.setRouteSet(routeSet);

            routeTableSet.getItem().add(routeTable);
        }

        ret.setRouteTableSet(routeTableSet);

        return ret;
    }

    /**
     * Handles "createRouteTable" request to create routetable and returns response with a route table.
     * @param vpcId vpc Id for Route Table.
     * @param cidrBlock VPC cidr block.
     * @return a CreateRouteTableResponseType with our new route table in  (or if not
     *         overridden, as defined in aws-mock-default.properties)
     */
    private CreateRouteTableResponseType createRouteTable(final String vpcId,
            final String cidrBlock) {
        CreateRouteTableResponseType ret = new CreateRouteTableResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        MockRouteTable mockRouteTable = mockRouteTableController.createRouteTable(cidrBlock, vpcId);

        RouteTableType routeTableType = new RouteTableType();
        routeTableType.setVpcId(mockRouteTable.getVpcId());
        routeTableType.setRouteTableId(mockRouteTable.getRouteTableId());

        ret.setRouteTable(routeTableType);
        return ret;
    }

    /**
     * Handles "createRoute" request to create route and returns response with a route table.
     * @param destinationCidrBlock : Route destinationCidrBlock.
     * @param internetGatewayId : for gateway Id.
     * @param routeTableId : for Route.
     * @return a CreateRouteResponseType with our new route.
     */
    private CreateRouteResponseType createRoute(final String destinationCidrBlock,
            final String internetGatewayId, final String routeTableId) {
        CreateRouteResponseType ret = new CreateRouteResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockRouteTableController.createRoute(destinationCidrBlock, internetGatewayId, routeTableId);

        return ret;
    }

    /**
     * Handles "createVolume" request to create volume and returns response with a volume.
     * @param volumeType of Volume.
     * @param size : Volume size.
     * @param availabilityZone : Volume availability zone.
     * @param iops : Volume iops count
     * @param snapshotId : Volume's SnapshotId.
     * @return a CreateRouteTableResponseType with our new route table in  (or if not
     *         overridden, as defined in aws-mock-default.properties)
     */
    private CreateVolumeResponseType createVolume(final String volumeType, final String size,
            final String availabilityZone,
            final int iops, final String snapshotId) {
        CreateVolumeResponseType ret = new CreateVolumeResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        MockVolume mockVolume = mockVolumeController.createVolume(volumeType, size,
                availabilityZone, iops, snapshotId);

        ret.setVolumeId(mockVolume.getVolumeId());
        ret.setVolumeType(mockVolume.getVolumeType());
        ret.setSize(mockVolume.getSize());
        ret.setAvailabilityZone(mockVolume.getAvailabilityZone());
        ret.setIops(mockVolume.getIops());
        ret.setSnapshotId(mockVolume.getSnapshotId());
        return ret;
    }

    /**
     * Handles "createSubnet" request to create Subnet and returns response with a subnet.
     * @param vpcId vpc Id for subnet.
     * @param cidrBlock VPC cidr block.
     * @return a CreateSubnetResponseType with our new Subnet
     */
    private CreateSubnetResponseType createSubnet(final String vpcId, final String cidrBlock) {
        CreateSubnetResponseType ret = new CreateSubnetResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        MockSubnet mockSubnet = mockSubnetController.createSubnet(cidrBlock, vpcId);

        SubnetType subnetType = new SubnetType();
        subnetType.setVpcId(mockSubnet.getVpcId());
        subnetType.setSubnetId(mockSubnet.getSubnetId());

        ret.setSubnet(subnetType);
        return ret;
    }

    /**
     * Handles "createInternetGateway" request to create InternetGateway and returns response with a InternetGateway.
     * @return a CreateInternetGatewayResponseType with our new InternetGateway
     */
    private CreateInternetGatewayResponseType createInternetGateway() {
        CreateInternetGatewayResponseType ret = new CreateInternetGatewayResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        MockInternetGateway mockInternetGateway = mockInternetGatewayController
                .createInternetGateway();
        InternetGatewayType internetGateway = new InternetGatewayType();
        internetGateway.setInternetGatewayId(mockInternetGateway.getInternetGatewayId());
        ret.setInternetGateway(internetGateway);
        return ret;
    }

    /**
     * Handles "deleteRouteTable" request to delete routetable and returns response with a route table.
     * @param routeTableId Route table Id.
     * @return a CreateRouteTableResponseType with route table
     */
    private DeleteRouteTableResponseType deleteRouteTable(final String routeTableId) {
        DeleteRouteTableResponseType ret = new DeleteRouteTableResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockRouteTableController.deleteRouteTable(routeTableId);
        return ret;
    }

    /**
     * Handles "deleteSubnet" request to delete subnet and returns response with a subnet.
     * @param subnetId Subnet Id.
     * @return a DeleteSubnetResponseType with subnet.
     */
    private DeleteSubnetResponseType deleteSubnet(final String subnetId) {
        DeleteSubnetResponseType ret = new DeleteSubnetResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockSubnetController.deleteSubnet(subnetId);
        return ret;
    }

    /**
     * Handles "deleteVolume" request to delete volume and returns response with a volume.
     * @param volumeId Volume Id.
     * @return a DeleteVolumeResponseType with volume.
     */
    private DeleteVolumeResponseType deleteVolume(final String volumeId) {
        DeleteVolumeResponseType ret = new DeleteVolumeResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockVolumeController.deleteVolume(volumeId);
        return ret;
    }

    /**
     * Handles "deleteInternetGateway" request to delete InternetGateway and returns response with a InternetGateway.
     * @param internetGatewayId : Internet Gateway Id.
     * @return a DeleteInternetGatewayResponseType with InternetGateway.
     */
    private DeleteInternetGatewayResponseType deleteInternetGateway(
            final String internetGatewayId) {
        DeleteInternetGatewayResponseType ret = new DeleteInternetGatewayResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockInternetGatewayController.deleteInternetGateway(internetGatewayId);
        return ret;
    }

    /**
     * Handles "deleteInternetGateway" request to delete InternetGateway and returns response with a InternetGateway.
     * @param internetgatewayId : Internet Gateway Id.
     * @param vpcId : vpc Id.
     * @return a DeleteInternetGatewayResponseType with InternetGateway.
     */
    private AttachInternetGatewayResponseType attachInternetGateway(final String internetgatewayId,
            final String vpcId) {
        AttachInternetGatewayResponseType ret = new AttachInternetGatewayResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockInternetGatewayController.attachInternetGateway(internetgatewayId, vpcId);
        ret.setReturn(true);
        return ret;
    }

    /**
     * Handles "describeVolumes" request and returns response with a volumes Set.
     * @param token
     *            token for next page
     * @param pMaxResults
     *            max result in page, if over 1000, only 1000 instances would be returned

     * @return a DescribeVolumesResponseType with our predefined route table in aws-mock.properties (or if not
     *         overridden, as defined in aws-mock-default.properties)
     */
    private DescribeVolumesResponseType describeVolumes(final String token, final int pMaxResults) {
        DescribeVolumesResponseType ret = new DescribeVolumesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        Set<String> idsInThisPageIfToken = null;

       if (null != token && token.length() > 0) {

           // should retrieve next page using token
            idsInThisPageIfToken = token2RemainingDescribedVolumeIDs.get(token);
           if (null == idsInThisPageIfToken) {
                // mock real AWS' 400 error message in case of invalid token
                throw new BadEc2RequestException("DescribeVolumes",
                        "AWS Error Code: InvalidParameterValue, AWS Error Message: Unable to parse pagination token");
            }
        }

        /**
         * The calculated maxResults used in pagination.
         */
        int maxResults = pMaxResults;

       if (maxResults < 1) {
            maxResults = MAX_RESULTS_DEFAULT;
        }

        List<String> idsToDescribe = null;

        if (null != token && token.length() > 0) {
            idsToDescribe = new ArrayList<String>(
                    token2RemainingDescribedVolumeIDs.remove(token));
        } else {
        // will return all instance IDs if the param 'instanceIDs' is empty here
            idsToDescribe = mockVolumeController.listVolumeIDs();
        }

        System.out.println(idsToDescribe);

       if (idsToDescribe.size() > maxResults) {
            // generate next token (for next page of results) and put the remaining IDs to the map for later use
            String newToken = generateToken();
            // deduct the current page instances from the total remaining and put the rest into map again, with new
            // token as key
           token2RemainingDescribedVolumeIDs.put(newToken,
                    new TreeSet<String>(idsToDescribe.subList(maxResults, idsToDescribe.size())));
            // set idsToDescribe as the top maxResults instance IDs
           idsToDescribe = new ArrayList<String>(idsToDescribe.subList(0, maxResults));
            // put the new token into response
           ret.setNextToken(newToken);
       }

       DescribeVolumesSetResponseType volumesSet = new DescribeVolumesSetResponseType();
       int recordCount = 1;
       for (Iterator<MockVolume> mockVolume = mockVolumeController.describeVolumes()
                .iterator(); mockVolume.hasNext();) {
            MockVolume item = mockVolume.next();
            System.out.println(idsToDescribe);
            System.out.println(item.getVolumeId());
            if (isVolumeIdExists(idsToDescribe, item.getVolumeId())) {
               DescribeVolumesSetItemResponseType volumesSetItem = new DescribeVolumesSetItemResponseType();
               volumesSetItem.setVolumeId(item.getVolumeId());
               volumesSetItem.setVolumeType(item.getVolumeType());
               volumesSetItem.setSize(item.getSize());
               volumesSetItem.setAvailabilityZone(item.getAvailabilityZone());
               volumesSetItem.setStatus(MOCK_VOLUME_STATUS);
               AttachmentSetResponseType attachmentSet = new AttachmentSetResponseType();

               AttachmentSetItemResponseType attachmentSetItem = new AttachmentSetItemResponseType();
               attachmentSetItem.setVolumeId(item.getVolumeId());
               attachmentSetItem.setInstanceId(MOCK_INSTANCE_ID);
               attachmentSetItem.setDevice("/dev/sdh");
               attachmentSetItem.setStatus("attached");
               attachmentSet.getItem().add(attachmentSetItem);
               volumesSetItem.setAttachmentSet(attachmentSet);

               volumesSet.getItem().add(volumesSetItem);
               recordCount++;
            }

            System.out.println("Max Count :" + maxResults);
            if (recordCount > maxResults) {
                break;
            }
        }

        ret.setVolumeSet(volumesSet);

        return ret;
    }

    /**
    * Check whether volumeId exists in list.
    * @param volumeIds List of volume Ids.
    * @param volumeId to check in the list.
    * @return true if volumeId is valid.
    */
    private boolean isVolumeIdExists(final List<String> volumeIds, final String volumeId) {
        if (volumeIds != null && volumeIds.size() > 0) {
            for (String volId : volumeIds) {
                if (volId.equals(volumeId)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handles "describeSubnets" request and returns response with a subnet Set.
     *
     * @return a DescribeSubnetsResponseType with Created Vpcs
     */
    private DescribeSubnetsResponseType describeSubnets() {
        DescribeSubnetsResponseType ret = new DescribeSubnetsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        SubnetSetType subnetSetType = new SubnetSetType();

        for (Iterator<MockSubnet> mockSubnet = mockSubnetController.describeSubnets()
                .iterator(); mockSubnet.hasNext();) {
            MockSubnet item = mockSubnet.next();
            SubnetType subnetType = new SubnetType();
            subnetType.setSubnetId(item.getSubnetId());
            subnetType.setState("available");
            subnetType.setVpcId(item.getVpcId());
            subnetType.setCidrBlock(item.getCidrBlock());
            subnetType.setAvailableIpAddressCount(item.getAvailableIpAddressCount());
            subnetType.setAvailabilityZone(DEFAULT_MOCK_PLACEMENT.getAvailabilityZone());
            subnetType.setDefaultForAz(false);
            subnetType.setMapPublicIpOnLaunch(false);

            subnetSetType.getItem().add(subnetType);
        }

        ret.setSubnetSet(subnetSetType);

        return ret;
    }

    /**
     * Handles "describeInternetGateways" request and returns response with a Internet gateway.
     *
     * @return a DescribeInternetGatewaysResponseType with Created InternetGateways.
     */
    private DescribeInternetGatewaysResponseType describeInternetGateways() {
        DescribeInternetGatewaysResponseType ret = new DescribeInternetGatewaysResponseType();
        InternetGatewaySetType internetGatewaySet = new InternetGatewaySetType();
        for (Iterator<MockInternetGateway> mockInternetGateway = mockInternetGatewayController
                .describeInternetGateways().iterator(); mockInternetGateway.hasNext();) {
            MockInternetGateway item = mockInternetGateway.next();

            InternetGatewayType internetGateway = new InternetGatewayType();
            internetGateway.setInternetGatewayId(item.getInternetGatewayId());
            InternetGatewayAttachmentSetType internetGatewayAttachmentSetType = new InternetGatewayAttachmentSetType();
            if (item.getAttachmentSet() != null && item.getAttachmentSet().size() > 0) {
                for (MockInternetGatewayAttachmentType mockInternetGatewayAttachementType : item
                        .getAttachmentSet()) {
                    InternetGatewayAttachmentType internetGatewayAttachmentType = new InternetGatewayAttachmentType();
                    internetGatewayAttachmentType
                            .setVpcId(mockInternetGatewayAttachementType.getVpcId());
                    internetGatewayAttachmentType
                            .setState(mockInternetGatewayAttachementType.getState());
                    internetGatewayAttachmentSetType.getItem().add(internetGatewayAttachmentType);

                }
            }
            internetGateway.setAttachmentSet(internetGatewayAttachmentSetType);

            internetGatewaySet.getItem().add(internetGateway);
        }

        ret.setInternetGatewaySet(internetGatewaySet);

        return ret;
    }

    /**
     * Handles "describeSecurityGroups" request and returns response with a security group.
     *
     * @return a DescribeInternetGatewaysResponseType with our predefined internet gateway in aws-mock.properties (or if
     *         not overridden, as defined in aws-mock-default.properties)
     */
    private DescribeSecurityGroupsResponseType describeSecurityGroups() {
        DescribeSecurityGroupsResponseType ret = new DescribeSecurityGroupsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        SecurityGroupSetType securityGroupSet = new SecurityGroupSetType();

        // initialize securityGroupItem
        SecurityGroupItemType securityGroupItem = new SecurityGroupItemType();
        securityGroupItem.setOwnerId(MOCK_SECURITY_OWNER_ID);
        securityGroupItem.setGroupName(MOCK_SECURITY_GROUP_NAME);
        securityGroupItem.setGroupId(MOCK_SECURITY_GROUP_ID);
        securityGroupItem.setVpcId(MOCK_VPC_ID);

        // initialize ipPermission
        IpPermissionType ipPermission = new IpPermissionType();
        ipPermission.setFromPort(MOCK_SOURCE_PORT);
        ipPermission.setToPort(MOCK_DEST_PORT);
        ipPermission.setIpProtocol(MOCK_IP_PROTOCOL);

        // initialize ipPermissionSet
        IpPermissionSetType ipPermissionSet = new IpPermissionSetType();
        ipPermissionSet.getItem().add(ipPermission);

        securityGroupItem.setIpPermissions(ipPermissionSet);

        securityGroupSet.getItem().add(securityGroupItem);
        ret.setSecurityGroupInfo(securityGroupSet);

        return ret;
    }

    /**
     * Handles "describeVpcs" request and returns response with a vpc.
     *
     * @return a DescribeVpcsResponseType with our predefined vpc in aws-mock.properties (or if not overridden, as
     *         defined in aws-mock-default.properties)
     */
    private DescribeVpcsResponseType describeVpcs() {
        DescribeVpcsResponseType ret = new DescribeVpcsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        VpcSetType vpcSet = new VpcSetType();
        for (Iterator<MockVpc> mockVpc = mockVpcController.describeVpcs().iterator(); mockVpc
                .hasNext();) {
            MockVpc item = mockVpc.next();

            VpcType vpcType = new VpcType();
            vpcType.setVpcId(item.getVpcId());
            vpcType.setState(item.getState());
            vpcType.setCidrBlock(item.getCidrBlock());
            vpcType.setIsDefault(item.getIsDefault());

            vpcSet.getItem().add(vpcType);
        }
        ret.setVpcSet(vpcSet);

        return ret;
    }

    /**
     * Handles "describeTags" request and returns response with a Tags.
     *
     * @return a DescribeTagsResponseType with our predefined Tags in aws-mock.properties (or if not overridden, as
     *         defined in aws-mock-default.properties)
     */
    private DescribeTagsResponseType describeTags() {
        DescribeTagsResponseType ret = new DescribeTagsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        TagSetType tagsSet = new TagSetType();
        for (MockTags mockVpc : mockTagsController.describeTags()) {

            for (String resourceId : mockVpc.getResourcesSet()) {
                TagSetItemType tagItem = new TagSetItemType();
                tagItem.setResourceId(resourceId);
                tagItem.setKey(mockVpc.getTagSet().keySet()
                        .toArray(new String[mockVpc.getTagSet().size()])[0]);
                tagItem.setValue(mockVpc.getTagSet().values()
                        .toArray(new String[mockVpc.getTagSet().size()])[0]);
                tagsSet.getItem().add(tagItem);
            }
        }

        ret.setTagSet(tagsSet);

        return ret;
    }

    /**
     * Handles "createTags" request and create new Tags.
     * @param resourcesSet List of resourceIds.
     * @param tagSet Map for key, value of tags.
     * @return a CreateTagsResponseType with Status of Tags.
     */
    private CreateTagsResponseType createTags(final List<String> resourcesSet,
            final Map<String, String> tagSet) {
        CreateTagsResponseType ret = new CreateTagsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockTagsController.createTags(resourcesSet, tagSet);
        ret.setReturn(true);
        return ret;
    }

    /**
     * Handles "deleteTags" request and delete Tags and returns response.
     * @param resources : List of resource Id to be deleted.
     * @return a DeleteTagsResponseType with Status
     */
    private DeleteTagsResponseType deleteTags(final List<String> resources) {

        DeleteTagsResponseType ret = new DeleteTagsResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockTagsController.deleteTags(resources);
        return ret;
    }

    /**
     * Handles "createVpc" request and create new Vpc.
     * @param cidrBlock : vpc cidrBlock.
     * @param instanceTenancy : vpc instanceTenancy.
     * @return a CreateVpcResponseType with new Vpc.
     */
    private CreateVpcResponseType createVpc(final String cidrBlock, final String instanceTenancy) {
        CreateVpcResponseType ret = new CreateVpcResponseType();
        ret.setRequestId(UUID.randomUUID().toString());

        MockVpc mockVpc = mockVpcController.createVpc(cidrBlock, instanceTenancy);
        VpcType vpcType = new VpcType();
        vpcType.setVpcId(mockVpc.getVpcId());
        vpcType.setState(mockVpc.getState());
        vpcType.setCidrBlock(mockVpc.getCidrBlock());
        vpcType.setIsDefault(mockVpc.getIsDefault());
        ret.setVpc(vpcType);

        return ret;
    }

    /**
     * Handles "deleteVpc" request and delete Vpc and returns response.
     * @param vpcId : Vpc Id to be deleted.
     * @return a DeleteVpcResponseType with new Vpc
     */
    private DeleteVpcResponseType deleteVpc(final String vpcId) {

        DeleteVpcResponseType ret = new DeleteVpcResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        mockVpcController.deleteVpc(vpcId);
        return ret;
    }

    /**
     * Generate error response body in xml and write it with writer.
     *
     * @param errorCode
     *            the error code wrapped in the xml response
     * @param errorMessage
     *            the error message wrapped in the xml response
     * @return xml body for an error message which can be recognized by AWS clients
     */
    private String getXmlError(final String errorCode, final String errorMessage) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errorCode", StringEscapeUtils.escapeXml(errorCode));
        data.put("errorMessage", StringEscapeUtils.escapeXml(errorMessage));
        // fake a random UUID as request ID
        data.put("requestID", UUID.randomUUID().toString());

        String ret = null;

        try {
            ret = TemplateUtils.get(ERROR_RESPONSE_TEMPLATE, data);
        } catch (AwsMockException e) {
            log.error("fatal exception caught: {}", e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

}
