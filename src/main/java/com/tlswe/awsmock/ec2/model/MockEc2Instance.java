package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;

import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.exception.MockEc2InternalException;

//import com.tlswe.awsmock.common.util.SerializedTimer;

/**
 * Generic implementation of mock ec2 instance, with basic simulation of behaviors and states of genuine ec2 instances.
 * Any extra implementation of more customized ec2 mock instances with is system should extend this class and be defined
 * as "ec2.instance.class" in aws-mock.properties. <br>
 * To simulate actual ec2 instances, we have an internal timer in each object of mock ec2 instance that continuously
 * check and set the states of it, within the life cycle of start-pending-running-stopping-stopped-terminated for a
 * single ec2 instance, and with random time deviations (e.g. random boot/shutdown time within predefined values).
 *
 * @author xma
 *
 */
public class MockEc2Instance implements Serializable {

    /**
     * Default serial version ID for this class which implements. {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Length of generated postfix of instance ID.
     */
    private static final short INSTANCE_ID_POSTFIX_LENGTH = 7;

    /**
     * All allowed instance types.
     *
     * @author xma
     *
     */
    public static enum InstanceType {
        /**
         * TODO javadoc.
         */
        T1_MICRO("t1.micro"),
        /**
         * TODO javadoc.
         */
        M1_SMALL("m1.small"),
        /**
         * TODO javadoc.
         */
        M1_MEDIUM("m1.medium"),
        /**
         * TODO javadoc.
         */
        M1_LARGE("m1.large"),
        /**
         * TODO javadoc.
         */
        M1_XLARGE("m1.xlarge"),
        /**
         * TODO javadoc.
         */
        M2_XLARGE("m2.xlarge"),
        /**
         * TODO javadoc.
         */
        M2_2XLARGE("m2.2xlarge"),
        /**
         * TODO javadoc.
         */
        M2_4XLARGE("m2.4xlarge"),
        /**
         * TODO javadoc.
         */
        C1_MEDIUM("c1.medium"),
        /**
         * TODO javadoc.
         */
        C1_XLARGE("c1.xlarge"),
        /**
         * TODO javadoc.
         */
        CC1_4XLARGE("cc1.4xlarge"),
        /**
         * TODO javadoc.
         */
        CC2_8XLARGE("cc2.8xlarge"),
        /**
         * TODO javadoc.
         */
        CG1_4XLARGE("cg1.4xlarge"),
        /**
         * TODO javadoc.
         */
        HI1_4XLARGE("hi1.4xlarge");

        /**
         * TODO javadoc.
         */
        private String name;

        /**
         * TODO javadoc.
         *
         * @param typeName
         *            TODO
         *
         */
        private InstanceType(final String typeName) {
            this.name = typeName;
        }

        /**
         * TODO javadoc.
         *
         * @return TODO
         */
        public String getName() {
            return this.name;
        }

        /**
         *
         * @param name
         *            TODO
         * @return TODO
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

    }

    /**
     * All allowed instance states.
     *
     * @author xma
     *
     */
    public static enum InstanceState {

        /**
         * TODO .
         */
        PENDING(0, "pending"),
        /**
         * TODO .
         */
        RUNNING(16, "running"),
        /**
         * TODO .
         */
        SHUTTING_DOWN(32, " shutting-down"),
        /**
         * TODO .
         */
        TERMINATED(48, "terminated"),
        /**
         * TODO .
         */
        STOPPING(64, "stopping"),
        /**
         * TODO .
         */
        STOPPED(80, "stopped");

        /**
         * TODO .
         */
        private int code;

        /**
         * TODO .
         */
        private String name;

        /**
         * TODO .
         *
         * @param stateCode
         *            TODO
         * @param stateName
         *            TODO
         */
        private InstanceState(final int stateCode, final String stateName) {
            this.code = stateCode;
            this.name = stateName;
        }

        /**
         * TODO .
         *
         * @return TODO
         */
        public int getCode() {
            return code;
        }

        /**
         * TODO .
         *
         * @return TODO
         */
        public String getName() {
            return name;
        }

    }

    /**
     * We define {@link Serializable} {@link Timer} here because all members in {@link MockEc2Instance} need to be save
     * to binary file as for persistence.
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
         *            TODO
         */
        public SerializableTimer(final boolean isDaemon) {
            super(isDaemon);
        }

    }

    /**
     * Interval for the internal timer thread that triggered for state chacking and changing - we set it for 10 seconds.
     */
    protected static final int TIMER_INTERVAL_MILLIS = 10 * 1000;

    /**
     * Utility random object for getting random numbers.
     */
    private static Random random = new Random();

    /**
     * Minimal boot time.
     */
    protected static final long MIN_BOOT_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty("instance.min.boot.time.seconds")) * 1000L;

    /**
     * Maximum boot time.
     */
    protected static final long MAX_BOOT_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty("instance.max.boot.time.seconds")) * 1000L;

    /**
     * Minimal shutdown time.
     */
    protected static final long MIN_SHUTDOWN_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty("instance.min.shutdown.time.seconds")) * 1000L;

    /**
     * maximum shutdown time.
     */
    protected static final long MAX_SHUTDOWN_TIME_MILLS = Integer
            .parseInt(PropertiesUtils
                    .getProperty("instance.max.shutdown.time.seconds")) * 1000L;

    /**
     * instance ID, randomly assigned on creating.
     */
    private String instanceID = null;

    /**
     * AMI for this ec2 instance.
     */
    private String imageId = null;

    /**
     * Instance type, default is "m1.small".
     */
    private String instanceType = InstanceType.M1_SMALL.getName();

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
    public MockEc2Instance() {
        if (null == this.instanceID) {
            this.instanceID = "i-"
                    + UUID.randomUUID().toString()
                            .substring(0, INSTANCE_ID_POSTFIX_LENGTH);
        }
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final String getInstanceID() {
        return instanceID;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final boolean isBooting() {
        return booting;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final String getPubDns() {
        return pubDns;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final boolean isStopping() {
        return stopping;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final boolean isTerminated() {
        return terminated;
    }

    /**
     * TODO .
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

                    try {

                        if (terminated) {
                            running = false;
                            booting = false;
                            stopping = false;

                            pubDns = null;
                            this.cancel();
                            return;
                        }

                        if (running) {

                            if (booting) {

                                // delay a random 'boot time'
                                try {
                                    Thread.sleep(MIN_BOOT_TIME_MILLS
                                            + random.nextInt((int) (MAX_BOOT_TIME_MILLS - MIN_BOOT_TIME_MILLS)));
                                } catch (InterruptedException e) {
                                    throw new MockEc2InternalException(
                                            "InterruptedException caught when delaying a mock random 'boot time'",
                                            e);
                                }

                                // booted, assign a mock pub dns name
                                pubDns = "mock-ec2-"
                                        + UUID.randomUUID().toString()
                                                .toLowerCase() + ".amazon.com";

                                booting = false;

                            } else if (stopping) {

                                // delay a random 'shutdown time'
                                try {
                                    Thread.sleep(MIN_SHUTDOWN_TIME_MILLS
                                            + random.nextInt((int) (MAX_SHUTDOWN_TIME_MILLS
                                                    - MIN_SHUTDOWN_TIME_MILLS)));
                                } catch (InterruptedException e) {
                                    throw new MockEc2InternalException(
                                            "InterruptedException caught when delaying a mock random 'shutdown time'",
                                            e);
                                }

                                // unset pub dns name
                                pubDns = null;

                                stopping = false;

                                running = false;

                            }

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            };
            timer = new SerializableTimer(true);
            timer.schedule(internalTimerTask, 0L, TIMER_INTERVAL_MILLIS);

            internalTimerInitialized = true;

        }
    }

    /**
     * TODO.
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
            terminated = true;
            return true;
        } else {
            return false;
        }

    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final InstanceState getInstanceState() {
        return isTerminated() ? InstanceState.TERMINATED
                : (isBooting() ? InstanceState.PENDING
                        : (isStopping() ? InstanceState.STOPPING
                                : (isRunning() ? InstanceState.RUNNING
                                        : InstanceState.STOPPED)));
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final String getImageId() {
        return imageId;
    }

    /**
     * TODO .
     *
     * @param newImageID
     *            TODO
     */
    public final void setImageId(final String newImageID) {
        this.imageId = newImageID;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final String getInstanceType() {
        return instanceType;
    }

    /**
     * TODO .
     *
     * @param newInstanceType
     *            TODO
     */
    public final void setInstanceType(final String newInstanceType) {
        this.instanceType = newInstanceType;
    }

    /**
     * TODO .
     *
     * @return TODO
     */
    public final Set<String> getSecurityGroups() {
        return securityGroups;
    }

    /**
     * TODO .
     *
     * @param newSecurityGroups
     *            TODO
     */
    public final void setSecurityGroups(final Set<String> newSecurityGroups) {
        if (null != newSecurityGroups) {
            this.securityGroups = newSecurityGroups;
        }
    }

}
