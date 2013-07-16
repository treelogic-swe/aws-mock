package com.tlswe.awsmock.ec2.control;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.xml.bind.JAXBException;

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
import com.tlswe.awsmock.ec2.util.JAXBUtil;

public class MockEC2QueryHandler {

    private static final String MOCK_EC2_INSTANCE_CLASS_NAME = PropertiesUtils.getProperty("ec2.instance.class");

    /**
     * 
     * @param queryParams
     * @param writer
     * @return
     */
    public static boolean writeReponse(Map<String, String[]> queryParams, final Writer writer) {

        if (null == queryParams || queryParams.size() == 0) {

            // TODO write an error xml response

            return false;
        }

        // parse the parameters in query
        String[] version = queryParams.get("Version");

        if (null == version || version.length != 1) {

        } else {
            // TODO write an error xml response
        }

        String[] action = queryParams.get("Action");

        if (null != action && action.length == 1) {

            if ("DescribeInstances".equals(action[0])) {

                // put all the instanceIDs into a set
                Set<String> instanceIDs = parseInstanceIDs(queryParams);

                try {
                    writer.write(JAXBUtil.marshall(describeInstances(instanceIDs), "DescribeInstancesResponse",
                            version[0]));
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if ("RunInstances".equals(action[0])) {

                String imageID = queryParams.get("ImageId")[0];
                String instanceType = queryParams.get("InstanceType")[0];
                int minCount = Integer.parseInt(queryParams.get("MinCount")[0]);
                int maxCount = Integer.parseInt(queryParams.get("MaxCount")[0]);

                try {
                    writer.write(JAXBUtil.marshall(runInstances(imageID, instanceType, minCount, maxCount),
                            "RunInstancesResponse", version[0]));
                    // JAXBUtil.marshall3(
                    // MockEC2QueryHandler.describeInstances(instanceIDs),
                    // writer);
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if ("StartInstances".equals(action[0])) {

                // put all the instanceIDs into a set
                Set<String> instanceIDs = parseInstanceIDs(queryParams);

                try {
                    writer.write(JAXBUtil.marshall(startInstances(instanceIDs), "StartInstancesResponse", version[0]));
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if ("StopInstances".equals(action[0])) {
                Set<String> instanceIDs = parseInstanceIDs(queryParams);

                try {
                    writer.write(JAXBUtil.marshall(stopInstances(instanceIDs), "StopInstancesResponse", version[0]));
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if ("TerminateInstances".equals(action[0])) {
                Set<String> instanceIDs = parseInstanceIDs(queryParams);

                try {
                    writer.write(JAXBUtil.marshall(terminateInstances(instanceIDs), "TerminateInstancesResponse",
                            version[0]));
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if ("DescribeImages".equals(action[0])) {
                try {
                    writer.write(JAXBUtil.marshall(describeImages(), "DescribeImagesResponse", version[0]));
                } catch (JAXBException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // unsupported action
            }

            return true;

        } else {

            return false;
        }

    }

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

                PlacementResponseType placement = new PlacementResponseType();
                placement.setAvailabilityZone(PropertiesUtils.getProperty("ec2.placement"));
                instItem.setPlacement(placement);

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

    @SuppressWarnings("unchecked")
    private static RunInstancesResponseType runInstances(String imageId, String instanceType, int minCount, int maxCount) {

        RunInstancesResponseType ret = new RunInstancesResponseType();

        RunningInstancesSetType instSet = new RunningInstancesSetType();

        Class<? extends MockEc2Instance> clazzOfEc2Instance = null;
        try {
            clazzOfEc2Instance = (Class<? extends MockEc2Instance>) Class.forName(MOCK_EC2_INSTANCE_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<MockEc2Instance> newInstances = null;
        try {
            newInstances = MockEc2Controller.runInstances(clazzOfEc2Instance, imageId, instanceType, /*
                                                                                                      * securityGroups
                                                                                                      * ,
                                                                                                      */
                    minCount, maxCount);
        } catch (MockEc2Exception e) {
            // TODO Auto-generated catch block

            // return failure xml structure

            e.printStackTrace();
        }

        for (MockEc2Instance i : newInstances) {

            RunningInstancesItemType riit = new RunningInstancesItemType();

            riit.setInstanceId(i.getInstanceID());
            riit.setImageId(i.getImageId());
            riit.setInstanceType(i.getInstanceType());
            InstanceStateType state = new InstanceStateType();
            state.setCode(i.getInstanceState().getCode());
            state.setName(i.getInstanceState().getName());
            riit.setInstanceState(state);
            riit.setDnsName(i.getPubDns());

            instSet.getItem().add(riit);

        }

        ret.setInstancesSet(instSet);

        return ret;

    }

    private static StartInstancesResponseType startInstances(Set<String> instanceIDs) {
        StartInstancesResponseType ret = new StartInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.startInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;

    }

    private static StopInstancesResponseType stopInstances(Set<String> instanceIDs) {
        StopInstancesResponseType ret = new StopInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.stopInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    private static TerminateInstancesResponseType terminateInstances(Set<String> instanceIDs) {
        TerminateInstancesResponseType ret = new TerminateInstancesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        InstanceStateChangeSetType changeSet = new InstanceStateChangeSetType();
        changeSet.getItem().addAll(MockEc2Controller.terminateInstances(instanceIDs));
        ret.setInstancesSet(changeSet);
        return ret;
    }

    private static DescribeImagesResponseType describeImages() {
        DescribeImagesResponseType ret = new DescribeImagesResponseType();
        ret.setRequestId(UUID.randomUUID().toString());
        DescribeImagesResponseInfoType info = new DescribeImagesResponseInfoType();
        DescribeImagesResponseItemType item = new DescribeImagesResponseItemType();
        item.setImageId("ami-12345678");

        info.getItem().add(item);
        ret.setImagesSet(info);

        return ret;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
