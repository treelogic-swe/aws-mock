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

/**
 * Factory class providing static methods for managing life cycle of mock ec2
 * instances. The current implementations can:
 * <ul>
 * <li>run</li>
 * <li>stop</li>
 * <li>terminate</li>
 * <li>describe</li>
 * </ul>
 * mock ec2 instances only. <br>
 * 
 * 
 * @author xma
 * 
 */
public class MockEc2Controller {

    /**
     * max allowed number of mock instances to run at a time (a single request)
     */
    private static final int MAX_RUN_INSTANCE_COUNT_AT_A_TIME = 10000;

    // private static final Random _random = new Random();

    /**
     * a map of all the mock ec2 instances, instanceID as key and
     * {@link MockEc2Instance} as value
     */
    private static Map<String, MockEc2Instance> _allMockEc2Instances = new ConcurrentHashMap<String, MockEc2Instance>();

    /**
     * List mock ec2 instances in current aws-mock.
     * 
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to
     *            describe
     * @return a collection of {@link MockEc2Instance} with specifed instance
     *         IDs, or all of the mock ec2 instances if no instance IDs as
     *         filtered
     */
    public static Collection<MockEc2Instance> describeInstances(Set<String> instanceIDs) {
        if (null == instanceIDs || instanceIDs.size() == 0) {
            return _allMockEc2Instances.values();
        } else {
            return getInstances(instanceIDs);
        }
    }

    /**
     * Create and run mock ec2 instances.
     * 
     * @param clazz
     *            class as type of mock ec2 instance to run as, should extend
     *            MockEc2Instance
     * @param imageId
     *            AMI of new mock ec2 instance(s)
     * @param instanceType
     *            type(scale) of new mock ec2 instance(s), refer to
     *            {@link MockEc2Instance#InstanceType}
     * @param minCount
     *            max count of instances to run (but limited to
     *            {@link #MAX_RUN_INSTANCE_COUNT_AT_A_TIME})
     * @param maxCount
     *            min count of instances to run (should larger than 0)
     * @return a list of objects of clazz as started new mock ec2 instances
     * @throws MockEc2Exception
     */
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

            inst.start();

            // internal timer should be initialized once right after mock ec2
            // instance is created and run
            inst.initializeInternalTimer();

            ret.add(inst);

            _allMockEc2Instances.put(inst.getInstanceID(), inst);

        }

        return ret;

    }

    /**
     * Start one or more existing mock ec2 instances.
     * 
     * @param instanceIDs
     *            a set of instance IDs for those instances to start
     * @return a list of state change messages (typically stopped to running)
     */
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

    /**
     * Stop one or more existing mock ec2 instances.
     * 
     * @param instanceIDs
     *            a set of instance IDs for those instances to stop
     * @return a list of state change messages (typically running to stopping)
     */
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

    /**
     * Terminate one or more existing mock ec2 instances.
     * 
     * @param instanceIDs
     *            a set of instance IDs for those instances to terminate
     * @return a list of state change messages (typically running/stopped to
     *         terminated)
     */
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

    /**
     * List all mock ec2 instances within aws-mock.
     * 
     * @return a collection of all the mock ec2 instances
     */
    public static Collection<MockEc2Instance> getAllMockEc2Instances() {
        return _allMockEc2Instances.values();
    }

    /**
     * Get mock ec2 instance by instance ID.
     * 
     * @param instanceID
     *            ID of the mock ec2 instance to get
     * @return the mock ec2 instance object
     */
    public static MockEc2Instance getMockEc2Instance(String instanceID) {
        return _allMockEc2Instances.get(instanceID);
    }

    /**
     * Get mock ec2 instances by instance IDs.
     * 
     * @param instanceIDs
     *            IDs of the mock ec2 instances to get
     * @return the mock ec2 instances object
     */
    private static Collection<MockEc2Instance> getInstances(Set<String> instanceIDs) {
        Collection<MockEc2Instance> ret = new ArrayList<MockEc2Instance>();
        for (String instanceID : instanceIDs) {
            ret.add(getMockEc2Instance(instanceID));
        }
        return ret;
    }

    /**
     * Clear {@link #_allMockEc2Instances} and restore it from given a
     * collection of instances.
     * 
     * @param instances
     *            collection of {@link #getMockEc2Instance(String)} to restore
     */
    public static void restoreAllMockEc2Instances(Collection<MockEc2Instance> instances) {
        _allMockEc2Instances.clear();
        if (null != instances) {
            for (MockEc2Instance instance : instances) {
                _allMockEc2Instances.put(instance.getInstanceID(), instance);
                // re-initialize the internal timer
                instance.initializeInternalTimer();
            }
        }
    }

}
