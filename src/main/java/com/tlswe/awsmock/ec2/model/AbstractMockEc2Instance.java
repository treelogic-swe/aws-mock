package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;

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
     * Length of generated postfix of instance ID.
     */
    protected static final short INSTANCE_ID_POSTFIX_LENGTH = 7;

    /**
     * Prefix for generated random public dnsname.
     */
    protected static final String MOCK_PUBDNS_PREFIX = "mock-ec2-";

    /**
     * Postfix for generated random public dnsname.
     */
    protected static final String MOCK_PUBDNS_POSTFIX = ".amazon.com";

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
     * We define {@link Serializable} {@link Timer} here because all members in {@link AbstractMockEc2Instance} need to
     * be save to binary file as for persistence.
     *
     * @author xma
     *
     */
    public class SerializableTimer extends Timer implements Serializable {

        /**
         * Default serial version ID for this class which implements {@link Serializable}.
         *
         * @see Serializable
         */
        private static final long serialVersionUID = 1L;


        /**
         * Constructor from superclass.
         */
        public SerializableTimer() {
            super();
        }


        /**
         * Constructor from superclass.
         *
         * @param isDaemon
         *            true if the associated thread should run as a daemon
         */
        public SerializableTimer(final boolean isDaemon) {
            super(isDaemon);
        }

    }

    /**
     * Interval for the internal timer thread that triggered for state chacking and changing - we set it for 10 seconds.
     */
    public static final int TIMER_INTERVAL_MILLIS = 10 * 1000;

    /**
     * Utility random object for getting random numbers.
     */
    private static Random random = new Random();

    /**
     * Minimal boot time.
     */
    public static final long MIN_BOOT_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty(Constants.PROP_NAME_INSTANCE_MIN_BOOT_TIME_SECONDS)) * 1000L;

    /**
     * Maximum boot time.
     */
    protected static final long MAX_BOOT_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty(Constants.PROP_NAME_INSTANCE_MAX_BOOT_TIME_SECONDS)) * 1000L;

    /**
     * Minimal shutdown time.
     */
    protected static final long MIN_SHUTDOWN_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty(Constants.PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME_SECONDS)) * 1000L;

    /**
     * maximum shutdown time.
     */
    protected static final long MAX_SHUTDOWN_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty(Constants.PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS)) * 1000L;

    /**
     * instance ID, randomly assigned on creating.
     */
    private String instanceID = null;

    /**
     * AMI for this ec2 instance.
     */
    private String imageId = null;

    /**
     * Instance type, by default is "m1.small".
     */
    private InstanceType instanceType = InstanceType.M1_SMALL;

    /**
     * Security groups for this ec2 instance.
     */
    private Set<String> securityGroups = new TreeSet<String>();

    /**
     * Flag that indicates whether internal timer of this mock ec2 instance has been started (on instance start()).
     */
    private boolean internalTimerInitialized = false;

    /**
     * Flag that indicates whether this is ec2 instance is booting (pending).
     */
    private boolean booting = false;

    /**
     * Flag that indicates whether this is ec2 instance is running (started).
     */
    private boolean running = false;

    /**
     * Flag that indicates whether this is ec2 instance is stopping (shutting-down).
     */
    private boolean stopping = false;

    /**
     * Flag that indicates whether this is ec2 instance is terminated.
     */
    private boolean terminated = false;

    /**
     * Randomly assigned public dns name for this ec2 instance (dns name is assigned each time instance is started).
     */
    private String pubDns = null;

    /**
     * Internal timer for simulating the behaviors and states of this mock ec2 instance.
     */
    private SerializableTimer timer = null;


    /**
     * On constructing, an instance ID is assigned.
     */
    public AbstractMockEc2Instance() {
        if (null == this.instanceID) {
            this.instanceID = "i-"
                    + UUID.randomUUID().toString()
                            .substring(0, INSTANCE_ID_POSTFIX_LENGTH);
        }
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
     * Start scheduling the internal timer that controls the behaviors and states of this mock ec2 instance.
     */
    public final void initializeInternalTimer() {
        // if it is the first the instance is started, we initialize the
        // internal timer thread
        if (!internalTimerInitialized) {

            TimerTask internalTimerTask = new TimerTask() {

                /**
                 * this method is triggered every TIMER_INTERVAL_MILLIS
                 */
                @Override
                public void run() {

                    if (terminated) {
                        running = false;
                        booting = false;
                        stopping = false;
                        pubDns = null;
                        this.cancel();
                        onTerminated();
                        return;
                    }

                    if (running) {

                        if (booting) {

                            // delay a random 'boot time'
                            try {
                                Thread.sleep(MIN_BOOT_TIME_MILLS
                                        + random.nextInt((int) (MAX_BOOT_TIME_MILLS - MIN_BOOT_TIME_MILLS)));
                            } catch (InterruptedException e) {
                                throw new AwsMockException(
                                        "InterruptedException caught when delaying a mock random 'boot time'",
                                        e);
                            }

                            // booted, assign a mock pub dns name
                            pubDns = generatePubDns();

                            booting = false;

                            onBooted();

                        } else if (stopping) {

                            // delay a random 'shutdown time'
                            try {
                                Thread.sleep(MIN_SHUTDOWN_TIME_MILLS
                                        + random.nextInt((int) (MAX_SHUTDOWN_TIME_MILLS
                                                - MIN_SHUTDOWN_TIME_MILLS)));
                            } catch (InterruptedException e) {
                                throw new AwsMockException(
                                        "InterruptedException caught when delaying a mock random 'shutdown time'",
                                        e);
                            }

                            // unset pub dns name
                            pubDns = null;

                            stopping = false;

                            running = false;

                            onStopped();

                        }

                        onInternalTimer();

                    }

                }
            };
            timer = new SerializableTimer(true);
            timer.schedule(internalTimerTask, 0L, TIMER_INTERVAL_MILLIS);

            internalTimerInitialized = true;
        }
    }


    /**
     * Cancel the internal timer of this mock ec2 instance so that it stops its lifecycle-emulation.
     */
    public final void destroyInternalTimer() {
        timer.cancel();
        timer = null;
        internalTimerInitialized = false;
    }


    /**
     * Start a stopped mock ec2 instance.
     *
     * @return true for successfully started and false for nothing changed by this action
     */
    public final boolean start() {

        if (running || booting || stopping || terminated) {
            // do nothing if this instance is not stopped
            return false;
        } else {
            // mark this instance started
            booting = true;
            running = true;

            onStarted();

            return true;
        }
    }


    /**
     * Stop this ec2 instance.
     *
     * @return true for successfully turned into 'stopping' and false for nothing changed
     */
    public final boolean stop() {

        if (booting || running) {
            stopping = true;
            booting = false;
            onStopping();
            return true;
        } else {
            return false;
        }

    }


    /**
     * Terminate this ec2 instance.
     *
     * @return true for successfully terminated and false for nothing changed
     */
    public final boolean terminate() {

        if (!terminated) {
            onTerminating();
            terminated = true;
            return true;
        } else {
            return false;
        }

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


    /**
     * Generate a random public dns name for a mock EC2 instance.
     *
     * @return a random public dns name
     */
    private String generatePubDns() {

        return MOCK_PUBDNS_PREFIX
                + UUID.randomUUID().toString()
                        .toLowerCase() + MOCK_PUBDNS_POSTFIX;
    }


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
