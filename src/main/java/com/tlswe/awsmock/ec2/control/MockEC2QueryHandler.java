package com.tlswe.awsmock.ec2.control;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tlswe.awsmock.common.util.PropertiesUtils;
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
import com.tlswe.awsmock.ec2.exception.MockEc2Exception;
import com.tlswe.awsmock.ec2.model.MockEc2Instance;
import com.tlswe.awsmock.ec2.servlet.MockEc2EndpointServlet;
import com.tlswe.awsmock.ec2.util.JAXBUtil;

/**
 * Class that handlers requests of AWS Query API for managing mock ec2
 * instances. This class works between {@link MockEc2Controller} and
 * {@link MockEc2EndpointServlet}. <br>
 * All object of mock ec2 instances are of the same type which is defined as
 * property of "ec2.instance.class" in aws-mock.properties.
 * 
 * @see MockEc2Controller
 * @see MockEc2EndpointServlet
 * 
 * @author xma
 * 
 */
public class MockEC2QueryHandler {

    /**
     * Log writer for this class.
     */
    private static Log _log = LogFactory.getLog(MockEC2QueryHandler.class);

    /**
     * class for all mock ec2 instances, which should extend
     * {@link MockEc2Instance}
     */
    private static final String MOCK_EC2_INSTANCE_CLASS_NAME = PropertiesUtils.getProperty("ec2.instance.class");

    /**
     * default placement for this aws-mock, defined in aws-mock.properties
     */
    private static final PlacementResponseType DEFAULT_MOCK_PLACEMENT = new PlacementResponseType();

    /**
     * predefined AMIs, as properties of predefined.mock.ami.X in
     * aws-mock.properties
     */
    private static final Set<String> MOCK_AMIS = new TreeSet<String>();

    static {
        DEFAULT_MOCK_PLACEMENT.setAvailabilityZone(PropertiesUtils.getProperty("ec2.placement"));
        MOCK_AMIS.addAll(PropertiesUtils.getPropertiesByPrefix("predefined.mock.ami."));
    }

    /**
     * Hub method for parsing query prarmeters and generate and write xml
     * response.
     * 
     * @param queryParams
     *            map of query parameters from http request, which is from
     *            standard AWS Query API
     * @param writer
     *            writer to put response into
     * @return true for successfully handling query, false: not
     * @throws
     */
    public static void writeReponse(Map<String, String[]> queryParams, final Writer writer) {

        if (null == queryParams || queryParams.size() == 0) {

            // TODO no params found at all - write an error xml response

            return;
        }

        // parse the parameters in query
        String[] version = queryParams.get("Version");

        if (null == version || version.length != 1) {

        } else {
            // TODO no version param found - write an error xml response
        }

        String[] action = queryParams.get("Action");

        String responseXml = null;

        if (null != action && action.length == 1) {

            try {

                if ("DescribeInstances".equals(action[0])) {

                    // put all the instanceIDs into a set
                    Set<String> instanceIDs = parseInstanceIDs(queryParams);

                    responseXml = JAXBUtil.marshall(describeInstances(instanceIDs), "DescribeInstancesResponse",
                            version[0]);

                } else if ("RunInstances".equals(action[0])) {

                    String imageID = queryParams.get("ImageId")[0];
                    String instanceType = queryParams.get("InstanceType")[0];
                    int minCount = Integer.parseInt(queryParams.get("MinCount")[0]);
                    int maxCount = Integer.parseInt(queryParams.get("MaxCount")[0]);

                    responseXml = JAXBUtil.marshall(runInstances(imageID, instanceType, minCount, maxCount),
                            "RunInstancesResponse", version[0]);

                } else if ("StartInstances".equals(action[0])) {

                    // put all the instanceIDs into a set
                    Set<String> instanceIDs = parseInstanceIDs(queryParams);

                    responseXml = JAXBUtil.marshall(startInstances(instanceIDs), "StartInstancesResponse", version[0]);

                } else if ("StopInstances".equals(action[0])) {
                    Set<String> instanceIDs = parseInstanceIDs(queryParams);

                    responseXml = JAXBUtil.marshall(stopInstances(instanceIDs), "StopInstancesResponse", version[0]);

                } else if ("TerminateInstances".equals(action[0])) {
                    Set<String> instanceIDs = parseInstanceIDs(queryParams);

                    responseXml = JAXBUtil.marshall(terminateInstances(instanceIDs), "TerminateInstancesResponse",
                            version[0]);

                } else if ("DescribeImages".equals(action[0])) {

                    responseXml = JAXBUtil.marshall(describeImages(), "DescribeImagesResponse", version[0]);

                } else {

                    // TODO unsupported action - write response for error

                }

            } catch (MockEc2Exception e) {
                _log.fatal("error occurred when processing 'runInstances' request: " + e.getMessage());
                // TODO write error xml response
            }

        } else {

            // TODO no action found - write response for error

            // responseXml = xxx;

        }

        try {
            writer.write(responseXml);
        } catch (IOException e) {
            _log.fatal("IOException caught while writing xml string to writer: " + e.getMessage());
        }

    }

    /**
     * Parse instance IDs from query parameters.
     * 
     * @param queryParams
     *            map of query parameters in http request
     * @return a set of instance IDs in the parameter map
     */
    private static Set<String> parseInstanceIDs(final Map<String, String[]> queryParams) {
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
     * Handles "describeInstances" request, with only a simplified filter of
     * instanceIDs, and returns response with all mock ec2 instances if no
     * instance IDs specified.
     * 
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to
     *            describe
     * @return a DescribeInstancesResponse with information for all mock ec2
     *         instances to describe
     */
    private static DescribeInstancesResponseType describeInstances(Set<String> instanceIDs) {

        DescribeInstancesResponseType ret = new DescribeInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        ReservationSetType resSet = new ReservationSetType();

        MockEc2Controller.getAllMockEc2Instances();

        Collection<MockEc2Instance> instances = MockEc2Controller.describeInstances(instanceIDs);

        for (MockEc2Instance instance : instances) {

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

                // PlacementResponseType placement = new
                // PlacementResponseType();
                // placement.setAvailabilityZone(PropertiesUtils.getProperty("ec2.placement"));
                instItem.setPlacement(DEFAULT_MOCK_PLACEMENT);

                InstanceStateType st = new InstanceStateType();
                st.setCode(instance.getInstanceState().getCode());
                st.setName(instance.getInstanceState().getName());

                instItem.setInstanceState(st);
                instItem.setImageId(instance.getImageId());
                instItem.setInstanceType(instance.getInstanceType());
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
     * Handles "runInstances" request, with only simplified filters of imageId,
     * instanceType, minCount and maxCount.
     * 
     * @param imageId
     *            AMI of new mock ec2 instance(s)
     * @param instanceType
     *            type(scale) of new mock ec2 instance(s), refer to
     *            {@link MockEc2Instance#InstanceType}
     * @param minCount
     *            max count of instances to run
     * @param maxCount
     *            min count of instances to run
     * @return a RunInstancesResponse that includes all information for the
     *         started new mock ec2 instances
     * @throws MockEc2Exception
     */
    @SuppressWarnings("unchecked")
    private static RunInstancesResponseType runInstances(String imageId, String instanceType, int minCount, int maxCount)
            throws MockEc2Exception {

        RunInstancesResponseType ret = new RunInstancesResponseType();

        RunningInstancesSetType instSet = new RunningInstancesSetType();

        Class<? extends MockEc2Instance> clazzOfEc2Instance = null;
        try {
            clazzOfEc2Instance = (Class<? extends MockEc2Instance>) Class.forName(MOCK_EC2_INSTANCE_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new MockEc2Exception("configured class '" + MOCK_EC2_INSTANCE_CLASS_NAME + "' not found", e);
        }

        List<MockEc2Instance> newInstances = null;

        newInstances = MockEc2Controller.runInstances(clazzOfEc2Instance, imageId, instanceType, minCount, maxCount);

        for (MockEc2Instance i : newInstances) {

            RunningInstancesItemType instItem = new RunningInstancesItemType();

            instItem.setInstanceId(i.getInstanceID());
            instItem.setImageId(i.getImageId());
            instItem.setInstanceType(i.getInstanceType());
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
     * Handles "startInstances" request, with only a simplified filter of
     * instanceIDs
     * 
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to
     *            start
     * @return a StartInstancesResponse with information for all mock ec2
     *         instances to start
     */
    private static StartInstancesResponseType startInstances(Set<String> instanceIDs) {
        StartInstancesResponseType ret = new StartInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.startInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;

    }

    /**
     * Handles "stopInstances" request, with only a simplified filter of
     * instanceIDs
     * 
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to
     *            stop
     * @return a StopInstancesResponse with information for all mock ec2
     *         instances to stop
     */
    private static StopInstancesResponseType stopInstances(Set<String> instanceIDs) {
        StopInstancesResponseType ret = new StopInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.stopInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    /**
     * Handles "terminateInstances" request, with only a simplified filter of
     * instanceIDs
     * 
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to
     *            terminate
     * @return a StartInstancesResponse with information for all mock ec2
     *         instances to terminate
     */
    private static TerminateInstancesResponseType terminateInstances(Set<String> instanceIDs) {
        TerminateInstancesResponseType ret = new TerminateInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.terminateInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    /**
     * Handles "describeImages" request, as simple as without any filters to use
     * 
     * @return a DescribeImagesResponse with our predefined AMIs in
     *         aws-mock.properties
     */
    private static DescribeImagesResponseType describeImages() {
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

}
