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
 * Generic implementation of mock ec2 instance, with basic simulation of
 * behaviors and states of genuine ec2 instances. Any extra implementation of
 * more customized ec2 mock instances with is system should extend this class
 * and be defined as "ec2.instance.class" in aws-mock.properties. <br>
 * To simulate actual ec2 instances, we have an internal timer in each object of
 * mock ec2 instance that continuously check and set the states of it, within
 * the life cycle of start-pending-running-stopping-stopped-terminated for a
 * single ec2 instance, and with random time deviations (e.g. random
 * boot/shutdown time within predefined values).
 * 
 * @author xma
 * 
 */
public class MockEc2Instance implements Serializable {

    /**
     * Default serial version ID for this class which implements.
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * All allowed instance types.
     * 
     * @author xma
     * 
     */
    public static enum InstanceType {
        T1_MICRO("t1.micro"), M1_SMALL("m1.small"), M1_MEDIUM("m1.medium"), M1_LARGE("m1.large"), M1_XLARGE("m1.xlarge"), M2_XLARGE(
                "m2.xlarge"), M2_2XLARGE("m2.2xlarge"), M2_4XLARGE("m2.4xlarge"), C1_MEDIUM("c1.medium"), C1_XLARGE(
                "c1.xlarge"), CC1_4XLARGE("cc1.4xlarge"), CC2_8XLARGE("cc2.8xlarge"), CG1_4XLARGE("cg1.4xlarge"), HI1_4XLARGE(
                "hi1.4xlarge");

        private String name;

        private InstanceType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static boolean containsByName(String name) {
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
     * all allowed instance states
     * 
     * @author xma
     * 
     */
    public static enum InstanceState {

        PENDING(0, "pending"), RUNNING(16, "running"), SHUTTING_DOWN(32, " shutting-down"), TERMINATED(48, "terminated"), STOPPING(
                64, "stopping"), STOPPED(80, "stopped");

        private int code;
        private String name;

        private InstanceState(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * We define {@link Serializable} {@link Timer} here because all members in
     * {@link MockEc2Instance} need to be save to binary file as for
     * persistence.
     * 
     * @author xma
     * 
     */
    public class SerializableTimer extends Timer implements Serializable {

        /**
         * default serial version ID for this class which implements
         * {@link Serializable}
         * 
         * @see Serializable
         */
        private static final long serialVersionUID = 1L;

        /**
         * constructor from superclass
         */
        public SerializableTimer() {
            super();
        }

        /**
         * constructor from superclass
         * 
         * @param isDaemon
         */
        public SerializableTimer(boolean isDaemon) {
            super(isDaemon);
        }

    }

    /**
     * interval for the internal timer thread that triggered for state chacking
     * and changing - we set it for 10 seconds
     */
    protected static final int TIMER_INTERVAL_MILLIS = 10 * 1000;

    /**
     * utility random object for getting random numbers
     */
    protected static Random _random = new Random();

    /**
     * minimal boot time
     */
    protected static final long MIN_BOOT_TIME_MILLS = Integer.parseInt(PropertiesUtils
            .getProperty("instance.min.boot.time.seconds")) * 1000L;

    /**
     * maximum boot time
     */
    protected static final long MAX_BOOT_TIME_MILLS = Integer.parseInt(PropertiesUtils
            .getProperty("instance.max.boot.time.seconds")) * 1000L;

    /**
     * minimal shutdown time
     */
    protected static final long MIN_SHUTDOWN_TIME_MILLS = Integer.parseInt(PropertiesUtils
            .getProperty("instance.min.shutdown.time.seconds")) * 1000L;

    /**
     * maximum shutdown time
     */
    protected static final long MAX_SHUTDOWN_TIME_MILLS = Integer.parseInt(PropertiesUtils
            .getProperty("instance.max.shutdown.time.seconds")) * 1000L;

    /**
     * instance ID, randomly assigned on creating
     */
    protected String instanceID = null;

    /**
     * AMI for this ec2 instance
     */
    protected String imageId = null;

    /**
     * instance type, default is "m1.small"
     */
    protected String instanceType = InstanceType.M1_SMALL.getName();

    /**
     * security groups for this ec2 instance
     */
    protected Set<String> securityGroups = new TreeSet<String>();

    /**
     * flag that indicates whether internal timer of this mock ec2 instance has
     * been started (on instance start())
     */
    protected boolean internalTimerInitialized = false;

    /**
     * flag that indicates whether this is ec2 instance is booting (pending)
     */
    protected boolean booting = false;

    /**
     * flag that indicates whether this is ec2 instance is running (started)
     */
    protected boolean running = false;

    /**
     * flag that indicates whether this is ec2 instance is stopping
     * (shutting-down)
     */
    protected boolean stopping = false;

    /**
     * flag that indicates whether this is ec2 instance is terminated
     */
    protected boolean terminated = false;

    /**
     * randomly assigned public dns name for this ec2 instance (dns name is
     * assigned each time instance is started)
     */
    protected String pubDns = null;

    /**
     * internal timer for simulating the behaviors and states of this mock ec2
     * instance
     */
    protected SerializableTimer timer = null;

    /**
     * on constructing, an instance ID is assigned
     */
    public MockEc2Instance() {
        if (null == this.instanceID) {
            this.instanceID = "i-" + UUID.randomUUID().toString().substring(0, 7);
        }
    }

    public String getInstanceID() {
        return instanceID;
    }

    public boolean isBooting() {
        return booting;
    }

    public boolean isRunning() {
        return running;
    }

    public String getPubDns() {
        return pubDns;
    }

    public boolean isStopping() {
        return stopping;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void initializeInternalTimer() {
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
                                            + _random.nextInt((int) (MAX_BOOT_TIME_MILLS - MIN_BOOT_TIME_MILLS)));
                                } catch (InterruptedException e) {
                                    throw new MockEc2InternalException(
                                            "InterruptedException caught when delaying a mock random 'boot time'", e);
                                }

                                // booted, assign a mock pub dns name
                                pubDns = "mock-ec2-" + UUID.randomUUID().toString().toLowerCase() + ".amazon.com";

                                booting = false;

                            } else if (stopping) {

                                // delay a random 'shutdown time'
                                try {
                                    Thread.sleep(MIN_SHUTDOWN_TIME_MILLS
                                            + _random
                                                    .nextInt((int) (MAX_SHUTDOWN_TIME_MILLS - MIN_SHUTDOWN_TIME_MILLS)));
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

    public void destroyInternalTimer() {
        timer.cancel();
        timer = null;
        internalTimerInitialized = false;
    }

    /**
     * Start a stopped mock ec2 instance.
     * 
     * @return true for successfully started and false for nothing changed by
     *         this action
     */
    public boolean start() {

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
     * @return true for successfully turned into 'stopping' and false for
     *         nothing changed
     */
    public boolean stop() {

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
    public boolean terminate() {

        if (!terminated) {
            terminated = true;
            return true;
        } else {
            return false;
        }

    }

    public InstanceState getInstanceState() {
        return isTerminated() ? InstanceState.TERMINATED : (isBooting() ? InstanceState.PENDING
                : (isStopping() ? InstanceState.STOPPING
                        : (isRunning() ? InstanceState.RUNNING : InstanceState.STOPPED)));
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public Set<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(Set<String> securityGroups) {
        if (null != securityGroups) {
            this.securityGroups = securityGroups;
        }
    }

}
