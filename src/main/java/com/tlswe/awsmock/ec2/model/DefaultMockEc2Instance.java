package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;

/**
 * Default type of mock ec2 instance (extends {@link AbstractMockEc2Instance}), using a timer to mock the lifecycle of
 * instance.
 *
 * @author xma
 *
 */
public class DefaultMockEc2Instance extends AbstractMockEc2Instance {

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
     * Internal timer for simulating the behaviors and states of this mock ec2 instance.
     */
    private SerializableTimer timer = null;

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


    public DefaultMockEc2Instance() {
        if (null == this.instanceID) {
            this.instanceID = "i-"
                    + UUID.randomUUID().toString()
                            .substring(0, INSTANCE_ID_POSTFIX_LENGTH);
        }
    }

    /**
     * Interval for the internal timer thread that triggered for state checking and changing - we set it for 10 seconds.
     */
    public static final int TIMER_INTERVAL_MILLIS = 10 * 1000;

    /**
     * Utility random object for getting random numbers.
     */
    private static Random random = new Random();

    /**
     * Minimal boot time.
     */
    public static final long MIN_BOOT_TIME_MILLS;

    /**
     * Maximum boot time.
     */
    protected static final long MAX_BOOT_TIME_MILLS;

    /**
     * Minimal shutdown time.
     */
    protected static final long MIN_SHUTDOWN_TIME_MILLS;

    /**
     * maximum shutdown time.
     */
    protected static final long MAX_SHUTDOWN_TIME_MILLS;

    /**
     * Millisecs in a second.
     */
    private static final long MILLISECS_IN_A_SECOND = 1000L;


    /**
     * Get millisecs from properties.
     *
     * @param propertyName
     *            the property name
     * @param propertyNameInSeconds
     *            the property name for seconds
     * @return millisecs
     */
    private static long getMsFromProperty(final String propertyName, final String propertyNameInSeconds) {
        String property = PropertiesUtils.getProperty(propertyName);
        if (property != null) {
            return Long.parseLong(property);
        }
        return Integer.parseInt(PropertiesUtils.getProperty(propertyNameInSeconds)) * MILLISECS_IN_A_SECOND;
    }

    static {
        MIN_BOOT_TIME_MILLS = getMsFromProperty(Constants.PROP_NAME_INSTANCE_MIN_BOOT_TIME,
                Constants.PROP_NAME_INSTANCE_MIN_BOOT_TIME_SECONDS);
        MAX_BOOT_TIME_MILLS = getMsFromProperty(Constants.PROP_NAME_INSTANCE_MAX_BOOT_TIME,
                Constants.PROP_NAME_INSTANCE_MAX_BOOT_TIME_SECONDS);
        MIN_SHUTDOWN_TIME_MILLS = getMsFromProperty(Constants.PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME,
                Constants.PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME_SECONDS);
        MAX_SHUTDOWN_TIME_MILLS = getMsFromProperty(Constants.PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME,
                Constants.PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS);
    }


    /**
     * Start scheduling the internal timer that controls the behaviors and states of this mock ec2 instance.
     */
    private final void initializeInternalTimer() {
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
                            if (MAX_BOOT_TIME_MILLS != 0) {
                                try {
                                    Thread.sleep(MIN_BOOT_TIME_MILLS
                                            + random.nextInt((int) (MAX_BOOT_TIME_MILLS - MIN_BOOT_TIME_MILLS)));
                                } catch (InterruptedException e) {
                                    throw new AwsMockException(
                                            "InterruptedException caught when delaying a mock random 'boot time'",
                                            e);
                                }
                            }

                            // booted, assign a mock pub dns name
                            pubDns = generatePubDns();

                            booting = false;

                            onBooted();

                        } else if (stopping) {

                            // delay a random 'shutdown time'
                            if (MAX_SHUTDOWN_TIME_MILLS != 0) {
                                try {
                                    Thread.sleep(MIN_SHUTDOWN_TIME_MILLS
                                            + random.nextInt((int) (MAX_SHUTDOWN_TIME_MILLS
                                                    - MIN_SHUTDOWN_TIME_MILLS)));
                                } catch (InterruptedException e) {
                                    throw new AwsMockException(
                                            "InterruptedException caught when delaying a mock random 'shutdown time'",
                                            e);
                                }
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
     * Generate a random public dns name for a mock EC2 instance.
     *
     * @return a random public dns name
     */
    private String generatePubDns() {

        return MOCK_PUBDNS_PREFIX
                + UUID.randomUUID().toString()
                        .toLowerCase() + MOCK_PUBDNS_POSTFIX;
    }


    @Override
    public void initBeforeStart() {

    }


    @Override
    public void initAfterStart() {
        // internal timer should be initialized once right after mock ec2
        // instance is created and run
        initializeInternalTimer();
    }


    /**
     * Start a stopped mock ec2 instance.
     *
     * @return true for successfully started and false for nothing changed by this action
     */
    @Override
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
    @Override
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
    @Override
    public final boolean terminate() {

        if (!terminated) {
            onTerminating();
            terminated = true;
            return true;
        } else {
            return false;
        }

    }


    @Override
    public final void destroy() {
        destroyInternalTimer();
    }


    @Override
    public void onStarted() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onBooted() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStopping() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStopped() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onTerminating() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onTerminated() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onInternalTimer() {
        // TODO Auto-generated method stub

    }

}
