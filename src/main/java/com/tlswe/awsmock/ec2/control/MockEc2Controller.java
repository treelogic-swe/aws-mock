package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateChangeType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateType;
import com.tlswe.awsmock.ec2.exception.MockEc2Exception;
import com.tlswe.awsmock.ec2.model.MockEc2Instance;

public class MockEc2Controller {

    private static final int MAX_RUN_INSTANCE_COUNT_AT_A_TIME = 10000;

    // private static final Random _random = new Random();

    /**
     * a map of all the mock ec2 instances, instanceID as key and
     * MockEc2InstanceLifecycleThread as value <br>
     * the states are simplified as: running, stopped
     */
    private static Map<String, MockEc2Instance> _allMockEc2Instances = new ConcurrentHashMap<String, MockEc2Instance>();

    public static Collection<MockEc2Instance> describeInstances(Set<String> instanceIDs) {
        if (null == instanceIDs || instanceIDs.size() == 0) {
            return _allMockEc2Instances.values();
        } else {
            return getInstances(instanceIDs);
        }
    }

    public static <T extends MockEc2Instance> List<T> runInstances(Class<? extends T> clazz, String imageId,
            String instanceType,
            /* Set<String> securityGroups, */int minCount, int maxCount) throws MockEc2Exception {

        if (!MockEc2Instance.InstanceType.containsByName(instanceType)) {
            throw new MockEc2Exception("illegal instance type: " + instanceType);
        }

        if (maxCount > MAX_RUN_INSTANCE_COUNT_AT_A_TIME) {
            throw new MockEc2Exception("you can not request to run more than " + MAX_RUN_INSTANCE_COUNT_AT_A_TIME
                    + " instances at a time!");
        }

        if (minCount < 1) {
            throw new MockEc2Exception("you should request to run at least 1 instance!");
        }

        if (minCount > maxCount) {
            throw new MockEc2Exception("minCount should not be greater than maxCount!");
        }

        List<T> ret = new ArrayList<T>();

        /*-
         * startup as much instances as possible
         */
        for (int i = 0; i < maxCount; i++) {

            T inst = null;
            try {
                inst = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new MockEc2Exception(
                        "failed to instantiate class "
                                + clazz.getName()
                                + ", please make sure sure this class extends com.tlswe.awsmock.ec2.model.MockEc2Instance and has a public constructor with no parameters. ",
                        e);
            } catch (IllegalAccessException e) {
                throw new MockEc2Exception("failed to access constructor of " + clazz.getName()
                        + ", please make sure the constructor with no parameters is public. ", e);
            }
            inst.setImageId(imageId);
            inst.setInstanceType(instanceType);
            // inst.setSecurityGroups(securityGroups);

            ret.add(inst);

            _allMockEc2Instances.put(inst.getInstanceID(), inst);

        }

        return ret;

    }

    public static List<InstanceStateChangeType> startInstances(Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<MockEc2Instance> instances = getInstances(instanceIDs);
        for (MockEc2Instance instance : instances) {
            if (null != instance) {
                InstanceStateChangeType stateChange = new InstanceStateChangeType();
                stateChange.setInstanceId(instance.getInstanceID());

                InstanceStateType previousState = new InstanceStateType();
                previousState.setCode(instance.getInstanceState().getCode());
                previousState.setName(instance.getInstanceState().getName());
                stateChange.setPreviousState(previousState);

                instance.start();

                InstanceStateType newState = new InstanceStateType();
                newState.setCode(instance.getInstanceState().getCode());
                newState.setName(instance.getInstanceState().getName());
                stateChange.setCurrentState(newState);

                ret.add(stateChange);
            }
        }

        return ret;

    }

    public static List<InstanceStateChangeType> stopInstances(Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<MockEc2Instance> instances = getInstances(instanceIDs);
        for (MockEc2Instance instance : instances) {
            if (null != instance) {
                InstanceStateChangeType stateChange = new InstanceStateChangeType();
                stateChange.setInstanceId(instance.getInstanceID());

                InstanceStateType previousState = new InstanceStateType();
                previousState.setCode(instance.getInstanceState().getCode());
                previousState.setName(instance.getInstanceState().getName());
                stateChange.setPreviousState(previousState);

                instance.stop();

                InstanceStateType newState = new InstanceStateType();
                newState.setCode(instance.getInstanceState().getCode());
                newState.setName(instance.getInstanceState().getName());
                stateChange.setCurrentState(newState);

                ret.add(stateChange);
            }
        }

        return ret;

    }

    public static List<InstanceStateChangeType> terminateInstances(Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<MockEc2Instance> instances = getInstances(instanceIDs);
        for (MockEc2Instance instance : instances) {
            if (null != instance) {
                InstanceStateChangeType stateChange = new InstanceStateChangeType();
                stateChange.setInstanceId(instance.getInstanceID());

                InstanceStateType previousState = new InstanceStateType();
                previousState.setCode(instance.getInstanceState().getCode());
                previousState.setName(instance.getInstanceState().getName());
                stateChange.setPreviousState(previousState);

                instance.terminate();

                InstanceStateType newState = new InstanceStateType();
                newState.setCode(instance.getInstanceState().getCode());
                newState.setName(instance.getInstanceState().getName());
                stateChange.setCurrentState(newState);

                ret.add(stateChange);
            }
        }

        return ret;

    }

    public static Collection<MockEc2Instance> getAllMockEc2Instances() {
        return _allMockEc2Instances.values();
    }

    public static MockEc2Instance getMockEc2Instance(String instanceID) {
        return _allMockEc2Instances.get(instanceID);
    }

    private static Collection<MockEc2Instance> getInstances(Set<String> instanceIDs) {
        Collection<MockEc2Instance> ret = new ArrayList<MockEc2Instance>();
        for (String instanceID : instanceIDs) {
            ret.add(getMockEc2Instance(instanceID));
        }
        return ret;
    }

    // /**
    // * Load mock ec2 intances along with mock vnc sessions on them, from the
    // * object map in {@link CoreHandler}, with is always made persistent. This
    // * method should be called once at mock appliation context initializing.
    // */
    // public static void restoreMockInstances() {
    // Collection<Ec2Node> nodes = CoreHandler.listAllNodes();
    // for (Ec2Node node : nodes) {
    // MockEc2Instance inst = _allMockEc2Instances.get(node
    // .getInstanceID());
    // if (null == inst) {
    //
    // inst = new MockEc2Instance(node.getInstanceID());
    //
    // Set<VncSession> vncSessionsOnNode = node.getAllVncSessions();
    // if (null != vncSessionsOnNode && vncSessionsOnNode.size() > 0) {
    // inst.restoreVncSessions(vncSessionsOnNode);
    // }
    //
    // _allMockEc2Instances.put(node.getInstanceID(), inst);
    //
    // }
    //
    // }
    // }

    /**
     * @param args
     */
    protected static void main(final String[] args) {
        // TODO Auto-generated method stub

    }

}
