package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generic class for mock ec2 instance, with basic simulation of behaviors and states of genuine ec2 instances' life
 * cycle. Any extra implementation of more customized ec2 mock instances should extend this class (and override the
 * events) and be defined as "ec2.instance.class" in aws-mock.properties (or if not overridden, as defined in
 * aws-mock-default.properties). To simulate actual ec2 instances, we have an internal timer in each object of mock ec2
 * instance that continuously check and set the states of it, within the life cycle of
 * start-pending-running-stopping-stopped-terminated for a single ec2 instance, and with random time deviations (e.g.
 * random boot/shutdown time within predefined values).
 *
 * @author xma
 *
 */
public abstract class AbstractMockEc2Instance implements Serializable {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of all allowed instance types.
     *
     * @author xma
     *
     */
    public static enum InstanceType {
        /**
         * t1.micro.
         */
        T1_MICRO("t1.micro"),
        /**
         * m1.small.
         */
        M1_SMALL("m1.small"),
        /**
         * m1.medium.
         */
        M1_MEDIUM("m1.medium"),
        /**
         * m1.large.
         */
        M1_LARGE("m1.large"),
        /**
         * m1.xlarge.
         */
        M1_XLARGE("m1.xlarge"),
        /**
         * m2.xlarge.
         */
        M2_XLARGE("m2.xlarge"),
        /**
         * m2.2xlarge.
         */
        M2_2XLARGE("m2.2xlarge"),
        /**
         * m2.4xlarge.
         */
        M2_4XLARGE("m2.4xlarge"),
        /**
         * c1.medium.
         */
        C1_MEDIUM("c1.medium"),
        /**
         * c1.xlarge.
         */
        C1_XLARGE("c1.xlarge"),
        /**
         * cc1.4xlarge.
         */
        CC1_4XLARGE("cc1.4xlarge"),
        /**
         * cc2.8xlarge.
         */
        CC2_8XLARGE("cc2.8xlarge"),
        /**
         * cg1.4xlarge.
         */
        CG1_4XLARGE("cg1.4xlarge"),
        /**
         * hi1.4xlarge.
         */
        HI1_4XLARGE("hi1.4xlarge");

        /**
         * Name of instacne type.
         */
        private String name;


        /**
         * Private constructor for the enums of instance types defined above.
         *
         * @param typeName
         *            name of instance type
         *
         */
        private InstanceType(final String typeName) {
            this.name = typeName;
        }


        /**
         * Get the instance type name.
         *
         * @return the instance type name of the enum.
         */
        public String getName() {
            return this.name;
        }


        /**
         * Tests if an instance type of the given name exists as among all the defined instance types.
         *
         * @param name
         *            instance type name
         * @return true for existing and false for not existing
         */
        public static boolean containsByName(final String name) {
            InstanceType[] values = InstanceType.values();
            for (InstanceType value : values) {
                if (value.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Get enum of an instance type of the given name exists as among all the defined instance types.
         *
         * @param name
         *            instance type name
         * @return object of instance type, null will be returned in case that not found
         */
        public static InstanceType getByName(final String name) {
            InstanceType[] values = InstanceType.values();
            for (InstanceType value : values) {
                if (value.getName().equals(name)) {
                    return value;
                }
            }
            return null;
        }

    }

    /**
     * Enumeration of all instance states.
     *
     * @author xma
     *
     */
    public static enum InstanceState {

        /**
         * pending.
         */
        PENDING(0, "pending"),
        /**
         * running.
         */
        RUNNING(16, "running"),
        /**
         * shutting-down.
         */
        SHUTTING_DOWN(32, "shutting-down"),
        /**
         * terminated.
         */
        TERMINATED(48, "terminated"),
        /**
         * stopping.
         */
        STOPPING(64, "stopping"),
        /**
         * stopped.
         */
        STOPPED(80, "stopped");

        /**
         * Code of instance state.
         */
        private int code;

        /**
         * Name of instance state.
         */
        private String name;


        /**
         * Private constructor for the enums of instance states defined above.
         *
         * @param stateCode
         *            code of instance state
         * @param stateName
         *            name of instance stateO
         */
        private InstanceState(final int stateCode, final String stateName) {
            this.code = stateCode;
            this.name = stateName;
        }


        /**
         * Get the instance state code.
         *
         * @return instance state code
         */
        public int getCode() {
            return code;
        }


        /**
         * Get the instance state name.
         *
         * @return instance state name
         */
        public String getName() {
            return name;
        }

    }

    /**
     * instance ID, randomly assigned on creating.
     */
    protected String instanceID = null;

    /**
     * AMI for this ec2 instance.
     */
    protected String imageId = null;

    /**
     * Instance type, by default is "m1.small".
     */
    protected InstanceType instanceType = InstanceType.M1_SMALL;

    /**
     * Security groups for this ec2 instance.
     */
    protected Set<String> securityGroups = new TreeSet<String>();

    /**
     * Flag that indicates whether internal timer of this mock ec2 instance has been started (on instance start()).
     */
    protected boolean internalTimerInitialized = false;

    /**
     * Flag that indicates whether this is ec2 instance is booting (pending).
     */
    protected boolean booting = false;

    /**
     * Flag that indicates whether this is ec2 instance is running (started).
     */
    protected boolean running = false;

    /**
     * Flag that indicates whether this is ec2 instance is stopping (shutting-down).
     */
    protected boolean stopping = false;

    /**
     * Flag that indicates whether this is ec2 instance is terminated.
     */
    protected boolean terminated = false;

    /**
     * Randomly assigned public dns name for this ec2 instance (dns name is assigned each time instance is started).
     */
    protected String pubDns = null;


    /**
     * On constructing, an instance ID is assigned.
     */
    public AbstractMockEc2Instance() {

    }


    /**
     * Get ID of this mock ec2 instance.
     *
     * @return ID of this mock ec2 instance
     */
    public final String getInstanceID() {
        return instanceID;
    }


    /**
     * Test if this mock ec2 instance is during booting phase (pending).
     *
     * @return this mock ec2 instance is booting or not
     */
    public final boolean isBooting() {
        return booting;
    }


    /**
     * Test if this mock ec2 instance is running (started/power-on).
     *
     * @return this mock ec2 instance is running or not
     */
    public final boolean isRunning() {
        return running;
    }


    /**
     * Get public DNS of this mock ec2 instance.
     *
     * @return public DNS of this mock ec2 instance
     */
    public final String getPubDns() {
        return pubDns;
    }


    /**
     * Test if this mock ec2 instance is during stopping phase.
     *
     * @return this mock ec2 instance is stopping or not
     */
    public final boolean isStopping() {
        return stopping;
    }


    /**
     * Test if this mock ec2 instance is terminated.
     *
     * @return this mock ec2 instance is terminated or not
     */
    public final boolean isTerminated() {
        return terminated;
    }


    /**
     * Get state of this mock ec2 instance.
     *
     * @return state of this mock ec2 instance, should be one of the instance states defined in {@link InstanceState}
     */
    public final InstanceState getInstanceState() {
        return isTerminated() ? InstanceState.TERMINATED
                : (isBooting() ? InstanceState.PENDING
                        : (isStopping() ? InstanceState.STOPPING
                                : (isRunning() ? InstanceState.RUNNING
                                        : InstanceState.STOPPED)));
    }


    /**
     * Get the AMI this mock ec2 instance started from.
     *
     * @return the AMI
     */
    public final String getImageId() {
        return imageId;
    }


    /**
     * Set the AMI this mock ec2 instance starts from.
     *
     * @param newImageID
     *            the AMI
     */
    public final void setImageId(final String newImageID) {
        this.imageId = newImageID;
    }


    /**
     * Get type of this mock ec2 instance.
     *
     * @return type of this mock ec2 instance
     */
    public final InstanceType getInstanceType() {
        return instanceType;
    }


    /**
     * Set type of this mock ec2 instance.
     *
     * @param newInstanceType
     *            type of this mock ec2 instance, should be one of the instance types defined in {@link InstanceType}
     */
    public final void setInstanceType(final InstanceType newInstanceType) {
        this.instanceType = newInstanceType;
    }


    /**
     * Get the security groups used by this mock ec2 instance.
     *
     * @return a list of security groups
     */
    public final Set<String> getSecurityGroups() {
        return securityGroups;
    }


    /**
     * Get the security groups used by this mock ec2 instance.
     *
     * @param newSecurityGroups
     *            a list of security groups
     */
    public final void setSecurityGroups(final Set<String> newSecurityGroups) {
        if (null != newSecurityGroups) {
            this.securityGroups = newSecurityGroups;
        }
    }


    public abstract void initBeforeStart();


    public abstract void initAfterStart();


    public abstract boolean start();


    public abstract boolean stop();


    public abstract boolean terminate();


    public abstract void destroy();


    /**
     * Triggered right after the 'instance' is 'powered-on'.
     */
    public abstract void onStarted();


    /**
     * Triggered after the 'instance' boots into 'OS'.
     */
    public abstract void onBooted();


    /**
     * Triggered on 'instance' entering the process of shutdown.
     */
    public abstract void onStopping();


    /**
     * Triggered right after the 'instance' is 'powered-off'.
     */
    public abstract void onStopped();


    /**
     * Triggered on 'instance' entering the process of termination.
     */
    public abstract void onTerminating();


    /**
     * Triggered right after the 'instance' is terminated.
     */
    public abstract void onTerminated();


    /**
     * Triggered on arriving interval of the internal timer of mock ec2 instance. <br>
     * Note that if this method does things that take a long time, the interval would be prolonged and consequent
     * events/state changing would always be delayed.
     *
     * @see #TIMER_INTERVAL_MILLIS
     */
    public abstract void onInternalTimer();

}
