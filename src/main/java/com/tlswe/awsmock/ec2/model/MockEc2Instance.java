package com.tlswe.awsmock.ec2.model;

import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;

public class MockEc2Instance {

    // private static Log _log = LogFactory.getLog(MockEc2Instance.class);

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
     * 
     */
    protected static Random _random = new Random();

    /**
     * 25 seconds
     */
    protected static final int MIN_BOOT_TIME_MILLS = 25 * 1000;

    /**
     * 75 seconds
     */
    protected static final int MAX_BOOT_TIME_MILLS = 75 * 1000;

    /**
     * 10 seconds
     */
    protected static final int MIN_SHUTDOWN_TIME_MILLS = 10 * 1000;

    /**
     * 30 seconds
     */
    protected static final int MAX_SHUTDOWN_TIME_MILLS = 30 * 1000;
    /**
     * 20 seconds
     */
    protected static final int TIMER_INTERVAL_MILLIS = 20 * 1000;

    /**
     * 5 minutes
     */
    protected static final int HEARTBEAT_INTERVAL_MILLIS = 300 * 1000;
    /**
     * 
     */
    protected String instanceID = null;
    // private String state = "pending";
    protected String imageId = null;

    protected String instanceType = InstanceType.M1_SMALL.getName();
    protected Set<String> securityGroups = new TreeSet<String>();

    protected boolean booting = true;
    protected boolean running = true;
    protected boolean stopping = false;
    protected boolean terminated = false;

    // protected InstanceState instanceState = InstanceState.PENDING;

    protected String pubDns = null;

    protected Timer timer = new Timer(true);

    protected int timerCounter = 0;

    public MockEc2Instance() {

        this.instanceID = "i-" + UUID.randomUUID().toString().substring(0, 7);

        timer.schedule(new TimerTask() {

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
                        timerCounter = 0;
                        pubDns = null;
                        this.cancel();
                        return;
                    }

                    if (running) {

                        if (booting) {

                            try {
                                Thread.sleep(MIN_BOOT_TIME_MILLS
                                        + _random.nextInt(MAX_BOOT_TIME_MILLS - MIN_BOOT_TIME_MILLS));
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            pubDns = "mock-ec2-" + UUID.randomUUID().toString().toLowerCase() + ".amazon.com";

                            booting = false;

                            timerCounter = 0;

                        } else if (stopping) {

                            try {
                                Thread.sleep(MIN_SHUTDOWN_TIME_MILLS
                                        + _random.nextInt(MAX_SHUTDOWN_TIME_MILLS - MIN_SHUTDOWN_TIME_MILLS));
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            pubDns = null;

                            stopping = false;

                            running = false;

                            timerCounter = 0;

                        }

                        if (timerCounter >= HEARTBEAT_INTERVAL_MILLIS / TIMER_INTERVAL_MILLIS) {

                            timerCounter = 0;

                        } else {

                            timerCounter++;

                        }

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, 0L, TIMER_INTERVAL_MILLIS);

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

    public boolean start() {
        if (running || booting || stopping || terminated) {
            return false;
        } else {
            timerCounter = 0;
            booting = true;
            running = true;
            return true;
        }
    }

    public boolean stop() {

        if (booting || running) {
            timerCounter = 0;
            stopping = true;
            booting = false;
            return true;
        } else {
            return false;
        }

    }

    public boolean terminate() {

        if (!terminated) {
            terminated = true;
            return true;
        } else {
            return false;
        }

    }

    // public String getStatusName() {
    // return isBooting() ? "pending" : (isStopping() ? "stopping" :
    // (isRunning() ? "running" : "stopped"));
    // }

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
