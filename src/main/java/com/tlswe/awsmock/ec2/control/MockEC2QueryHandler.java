package com.tlswe.awsmock.ec2.control;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.common.util.TemplateUtils;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseItemType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeImagesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.DescribeInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.GroupItemType;
import com.tlswe.awsmock.ec2.cxf_generated.GroupSetType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateChangeSetType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateType;
import com.tlswe.awsmock.ec2.cxf_generated.PlacementResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.ReservationInfoType;
import com.tlswe.awsmock.ec2.cxf_generated.ReservationSetType;
import com.tlswe.awsmock.ec2.cxf_generated.RunInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesItemType;
import com.tlswe.awsmock.ec2.cxf_generated.RunningInstancesSetType;
import com.tlswe.awsmock.ec2.cxf_generated.StartInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.StopInstancesResponseType;
import com.tlswe.awsmock.ec2.cxf_generated.TerminateInstancesResponseType;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
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
     * Instance of {@link MockEc2Controller} uesed in this class that controls mock EC2 instances.
     */
    private final MockEc2Controller mockEc2Controller = MockEc2Controller.getInstance();

    static {
        DEFAULT_MOCK_PLACEMENT.setAvailabilityZone(PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_PLACEMENT));
        MOCK_AMIS.addAll(PropertiesUtils.getPropertiesByPrefix("predefined.mock.ami."));
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
            responseXml = getXmlError("InvalidQuery", "No parameter in query at all! " + REF_EC2_QUERY_API_DESC);
        } else {
            // parse the parameters in query
            String[] versionParamValues = queryParams.get("Version");

            if (null == versionParamValues || versionParamValues.length != 1) {
                // no version param found - write an error xml response
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseXml = getXmlError("InvalidQuery",
                        "There should be a parameter of 'Version' provided in the query! " + REF_EC2_QUERY_API_DESC);
            } else {

                String version = versionParamValues[0];

                String[] actions = queryParams.get("Action");

                if (null == actions || actions.length != 1) {
                    // no action found - write response for error
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseXml = getXmlError("InvalidQuery",
                            "There should be a parameter of 'Action' provided in the query! " + REF_EC2_QUERY_API_DESC);
                } else {

                    String action = actions[0];

                    try {

                        response.setStatus(HttpServletResponse.SC_OK);

                        if ("RunInstances".equals(action)) {

                            String imageID = queryParams.get("ImageId")[0];
                            String instanceType = queryParams.get("InstanceType")[0];
                            int minCount = Integer.parseInt(queryParams.get("MinCount")[0]);
                            int maxCount = Integer.parseInt(queryParams.get("MaxCount")[0]);

                            responseXml = JAXBUtil.marshall(runInstances(imageID, instanceType, minCount, maxCount),
                                    "RunInstancesResponse", version);

                        } else if ("DescribeImages".equals(action)) {
                            responseXml = JAXBUtil.marshall(describeImages(), "DescribeImagesResponse", version);
                        } else {

                            // the following interface calls need instanceIDs
                            // provided
                            // in params, we put all the instanceIDs into a set
                            // for
                            // usage
                            Set<String> instanceIDs = parseInstanceIDs(queryParams);

                            if ("DescribeInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(describeInstances(instanceIDs),
                                        "DescribeInstancesResponse", version);

                            } else if ("StartInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(startInstances(instanceIDs), "StartInstancesResponse",
                                        version);

                            } else if ("StopInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(stopInstances(instanceIDs), "StopInstancesResponse",
                                        version);

                            } else if ("TerminateInstances".equals(action)) {

                                responseXml = JAXBUtil.marshall(terminateInstances(instanceIDs),
                                        "TerminateInstancesResponse", version);

                            } else {

                                // unsupported/unimplemented action - write an
                                // error
                                // response
                                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                                String allImplementedActions = "runInstances|stopInstances|startInstances|"
                                        + "terminateInstances|describeInstances|describeImages";
                                responseXml = getXmlError("NotImplementedAction", "Action '" + action
                                        + "' has not been implemented yet in aws-mock. "
                                        + "For now we only support actions as following: " + allImplementedActions);
                            }
                        }

                    } catch (BadEc2RequestException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseXml = getXmlError("InvalidQuery",
                                "invalid request for '" + action + "'. " + e.getMessage() + REF_EC2_QUERY_API_DESC);
                    } catch (AwsMockException e) {
                        log.error("server error occured while processing '{}' request. {}", action, e.getMessage());
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
            if (null != entry && null != entry.getKey() && entry.getKey().matches("InstanceId\\.(\\d)+")) {
                if (null != entry.getValue() && entry.getValue().length > 0) {
                    ret.add(entry.getValue()[0]);
                }
            }
        }
        return ret;
    }


    /**
     * Handles "describeInstances" request, with only a simplified filter of instanceIDs, and returns response with all
     * mock ec2 instances if no instance IDs specified.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to describe
     * @return a DescribeInstancesResponse with information for all mock ec2 instances to describe
     */
    private DescribeInstancesResponseType describeInstances(final Set<String> instanceIDs) {

        DescribeInstancesResponseType ret = new DescribeInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        ReservationSetType resSet = new ReservationSetType();

        mockEc2Controller.getAllMockEc2Instances();

        Collection<AbstractMockEc2Instance> instances = mockEc2Controller.describeInstances(instanceIDs);

        for (AbstractMockEc2Instance instance : instances) {

            if (null != instance) {

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

                instsSet.getItem().add(instItem);

                resInfo.setInstancesSet(instsSet);

                resSet.getItem().add(resInfo);

            }

        }

        ret.setReservationSet(resSet);

        return ret;

    }


    /**
     * Handles "runInstances" request, with only simplified filters of imageId, instanceType, minCount and maxCount.
     *
     * @param imageId
     *            AMI of new mock ec2 instance(s)
     * @param instanceType
     *            type(scale) of new mock ec2 instance(s), refer to {@link AbstractMockEc2Instance#InstanceType}
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
            throw new AwsMockException("badly configured class '" + MOCK_EC2_INSTANCE_CLASS_NAME + "' not found", e);
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
