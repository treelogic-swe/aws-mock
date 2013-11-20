package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateChangeType;
import com.tlswe.awsmock.ec2.cxf_generated.InstanceStateType;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;

/**
 * Factory class providing static methods for managing life cycle of mock ec2 instances. The current implementations
 * can:
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
public final class MockEc2Controller {

    /**
     * Singleton instance of MockEc2Controller.
     */
    private static MockEc2Controller singletonMockEc2Controller = null;

    /**
     * Max allowed number of mock instances to run at a time (a single request).
     */
    private static final int MAX_RUN_INSTANCE_COUNT_AT_A_TIME = 10000;

    // private static final Random _random = new Random();

    /**
     * A map of all the mock ec2 instances, instanceID as key and {@link AbstractMockEc2Instance} as value.
     */
    private final Map<String, AbstractMockEc2Instance> allMockEc2Instances =
            new ConcurrentHashMap<String, AbstractMockEc2Instance>();


    /**
     * Constructor of MockEc2Controller is made private and only called once by {@link #getInstance()}.
     */
    private MockEc2Controller() {

    }


    /**
     *
     * @return singleton instance of {@link MockEc2Controller}
     */
    public static MockEc2Controller getInstance() {
        if (null == singletonMockEc2Controller) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockEc2Controller.class) {
                if (null == singletonMockEc2Controller) {
                    singletonMockEc2Controller = new MockEc2Controller();
                }
            }
        }
        return singletonMockEc2Controller;
    }


    /**
     * List mock ec2 instances in current aws-mock.
     *
     * @param instanceIDs
     *            a filter of specified instance IDs for the target instance to describe
     * @return a collection of {@link AbstractMockEc2Instance} with specifed instance IDs, or all of the mock ec2
     *         instances if no instance IDs as filtered
     */
    public Collection<AbstractMockEc2Instance> describeInstances(final Set<String> instanceIDs) {
        if (null == instanceIDs || instanceIDs.size() == 0) {
            return allMockEc2Instances.values();
        } else {
            return getInstances(instanceIDs);
        }
    }


    /**
     *
     * Create and run mock ec2 instances.
     *
     * @param <T>
     *            The template type of class as concrete type of mock ec2 instance to run as, should extend
     *            {@link AbstractMockEc2Instance}, matching the clazz parameter
     * @param clazz
     *            class as concrete type of mock ec2 instance to run as, should extend {@link AbstractMockEc2Instance}
     * @param imageId
     *            AMI of new mock ec2 instance(s)
     * @param instanceTypeName
     *            type(scale) name of new mock ec2 instance(s)
     * @param minCount
     *            max count of instances to run (but limited to {@link #MAX_RUN_INSTANCE_COUNT_AT_A_TIME})
     * @param maxCount
     *            min count of instances to run (should larger than 0)
     * @return a list of objects of clazz as started new mock ec2 instances
     *
     */
    public <T extends AbstractMockEc2Instance> List<T> runInstances(final Class<? extends T> clazz,
            final String imageId, final String instanceTypeName,
            final int minCount, final int maxCount) {

        // EC2 Query Request action name
        final String action = "runInstances";

        /*-
         * throws an exception in case of error parsing for a correct request conformed to EC2 QUERY API which
         *             should be built by AWS client tool correctly
         */
        InstanceType instanceType = InstanceType.getByName(instanceTypeName);
        if (null == instanceType) {
            throw new BadEc2RequestException(action, "illegal instance type: " + instanceTypeName);
        }

        if (maxCount > MAX_RUN_INSTANCE_COUNT_AT_A_TIME) {
            throw new BadEc2RequestException(action, "you can not request to run more than "
                    + MAX_RUN_INSTANCE_COUNT_AT_A_TIME
                    + " instances at a time!");
        }

        if (minCount < 1) {
            throw new BadEc2RequestException(action, "you should request to run at least 1 instance!");
        }

        if (minCount > maxCount) {
            throw new BadEc2RequestException(action, "minCount should not be greater than maxCount!");
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
                throw new AwsMockException("failed to instantiate class " + clazz.getName()
                        + ", please make sure sure this class extends com.tlswe.awsmock.ec2.model.MockEc2Instance "
                        + "and has a public constructor with no parameters. ", e);
            } catch (IllegalAccessException e) {
                throw new AwsMockException("failed to access constructor of " + clazz.getName()
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

            allMockEc2Instances.put(inst.getInstanceID(), inst);

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
    public List<InstanceStateChangeType> startInstances(final Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<AbstractMockEc2Instance> instances = getInstances(instanceIDs);
        for (AbstractMockEc2Instance instance : instances) {
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
    public List<InstanceStateChangeType> stopInstances(final Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<AbstractMockEc2Instance> instances = getInstances(instanceIDs);
        for (AbstractMockEc2Instance instance : instances) {
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
     * @return a list of state change messages (typically running/stopped to terminated)
     */
    public List<InstanceStateChangeType> terminateInstances(final Set<String> instanceIDs) {

        List<InstanceStateChangeType> ret = new ArrayList<InstanceStateChangeType>();

        Collection<AbstractMockEc2Instance> instances = getInstances(instanceIDs);
        for (AbstractMockEc2Instance instance : instances) {
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
    public Collection<AbstractMockEc2Instance> getAllMockEc2Instances() {
        return allMockEc2Instances.values();
    }


    /**
     * Get mock ec2 instance by instance ID.
     *
     * @param instanceID
     *            ID of the mock ec2 instance to get
     * @return the mock ec2 instance object
     */
    public AbstractMockEc2Instance getMockEc2Instance(final String instanceID) {
        return allMockEc2Instances.get(instanceID);
    }


    /**
     * Get mock ec2 instances by instance IDs.
     *
     * @param instanceIDs
     *            IDs of the mock ec2 instances to get
     * @return the mock ec2 instances object
     */
    private Collection<AbstractMockEc2Instance> getInstances(final Set<String> instanceIDs) {
        Collection<AbstractMockEc2Instance> ret = new ArrayList<AbstractMockEc2Instance>();
        for (String instanceID : instanceIDs) {
            ret.add(getMockEc2Instance(instanceID));
        }
        return ret;
    }


    /**
     * Clear {@link #allMockEc2Instances} and restore it from given a collection of instances.
     *
     * @param instances
     *            collection of {@link #getMockEc2Instance(String)} to restore
     */
    public void restoreAllMockEc2Instances(final Collection<AbstractMockEc2Instance> instances) {
        allMockEc2Instances.clear();
        if (null != instances) {
            for (AbstractMockEc2Instance instance : instances) {
                allMockEc2Instances.put(instance.getInstanceID(), instance);
                // re-initialize the internal timer
                instance.initializeInternalTimer();
            }
        }
    }

}
