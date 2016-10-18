package com.tlswe.awsmock.ec2.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceState;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance.InstanceType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AbstractMockEc2Instance.class})
public class DefaultMockEc2InstanceTest {

    private static final String MAX_BOOT_TIME_MILLS = "MAX_BOOT_TIME_MILLS";
    private static final String MAX_SHUTDOWN_TIME_MILLS = "MAX_SHUTDOWN_TIME_MILLS";
    private static final int TIMER_SLEEP_MILLIS = 1000;
    private static final String AMI_234 = "ami-234";

    @Test
    public void Test_allOverriddenMethods(){

        DefaultMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();
        defaultMockEc2Instance.onStarted();
        defaultMockEc2Instance.onBooted();
        defaultMockEc2Instance.onStopping();
        defaultMockEc2Instance.onStopped();
        defaultMockEc2Instance.onTerminating();
        defaultMockEc2Instance.onTerminated();
        defaultMockEc2Instance.onInternalTimer();
    }

    @Test
    public void Test_instanceTypeEnum(){

        InstanceType instanceType = InstanceType.C1_XLARGE;

        Assert.assertTrue("c1.xlarge".equals(instanceType.getName()));
        Assert.assertTrue(InstanceType.containsByName("c1.xlarge"));
        Assert.assertFalse(InstanceType.containsByName("test"));
        Assert.assertTrue(InstanceType.getByName("c1.xlarge") == instanceType);
        Assert.assertNull(InstanceType.getByName("test"));
    }

    @Test
    public void Test_instanceStateEnum(){

        InstanceState instanceState = InstanceState.RUNNING;

        Assert.assertTrue("running".equals(instanceState.getName()));
        Assert.assertTrue(16 == instanceState.getCode());
    }

    @Test
    public void Test_getInstanceID(){

        DefaultMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();
        Assert.assertNotNull(defaultMockEc2Instance.getInstanceID());
        Assert.assertTrue(defaultMockEc2Instance.getInstanceID().startsWith("i-"));
    }

    @Test
    public void Test_routineLifeCycleMethodsForInstance(){

        DefaultMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();
        defaultMockEc2Instance.setInstanceType(InstanceType.C1_MEDIUM);
        defaultMockEc2Instance.setImageId(AMI_234);
        defaultMockEc2Instance.start();

        Assert.assertTrue(defaultMockEc2Instance.isBooting());
        Assert.assertTrue(defaultMockEc2Instance.isRunning());

        boolean ret = defaultMockEc2Instance.stop();

        Assert.assertTrue(ret);

        Assert.assertTrue(defaultMockEc2Instance.isStopping());

        ret = defaultMockEc2Instance.terminate();

        Assert.assertTrue(ret);

        // simply try to terminate again, this should fail
        ret = defaultMockEc2Instance.terminate();

        Assert.assertFalse(ret);

        Assert.assertTrue(defaultMockEc2Instance.isTerminated());

        // check instance state
        Assert.assertTrue(defaultMockEc2Instance.getInstanceState()==InstanceState.TERMINATED);

        // check instance type
        Assert.assertTrue(defaultMockEc2Instance.getInstanceType()==InstanceType.C1_MEDIUM);

        // check image
        Assert.assertTrue(defaultMockEc2Instance.getImageId().equals(AMI_234));
    }

    @Test
    public void Test_timerInstanceStarted() throws Exception{

        DefaultMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();

        defaultMockEc2Instance.setInstanceType(InstanceType.C1_MEDIUM);
        defaultMockEc2Instance.setImageId(AMI_234);
        defaultMockEc2Instance.start();

        defaultMockEc2Instance.initializeInternalTimer();

        Thread.sleep(TIMER_SLEEP_MILLIS);

        defaultMockEc2Instance.destroyInternalTimer();
    }

    @Test
    public void Test_timerInstanceStartedMaxBoot0() throws Exception{

        setFinalStatic(AbstractMockEc2Instance.class.getDeclaredField(MAX_BOOT_TIME_MILLS), 0);
        AbstractMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();

        defaultMockEc2Instance.setInstanceType(InstanceType.C1_MEDIUM);
        defaultMockEc2Instance.setImageId(AMI_234);
        defaultMockEc2Instance.start();

        defaultMockEc2Instance.initializeInternalTimer();

        Thread.sleep(TIMER_SLEEP_MILLIS);

        defaultMockEc2Instance.destroyInternalTimer();

        // reset the value
        long resetValue = Whitebox.invokeMethod(defaultMockEc2Instance, "getMsFromProperty", Constants.PROP_NAME_INSTANCE_MAX_BOOT_TIME,
                Constants.PROP_NAME_INSTANCE_MAX_BOOT_TIME_SECONDS);

        setFinalStatic(AbstractMockEc2Instance.class.getDeclaredField(MAX_BOOT_TIME_MILLS), resetValue);

        Assert.assertFalse(defaultMockEc2Instance.isBooting());
        Assert.assertNotNull(defaultMockEc2Instance.getPubDns());
    }

    @Test
    public void Test_timerInstanceStopped() throws InterruptedException{

        DefaultMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();
        defaultMockEc2Instance.setInstanceType(InstanceType.C1_MEDIUM);
        defaultMockEc2Instance.setImageId(AMI_234);
        defaultMockEc2Instance.start();
        defaultMockEc2Instance.stop();

        defaultMockEc2Instance.initializeInternalTimer();

        Thread.sleep(TIMER_SLEEP_MILLIS);

        defaultMockEc2Instance.destroyInternalTimer();
    }

    @Test
    public void Test_timerInstanceStoppedMaxShutdown0() throws Exception{

        setFinalStatic(AbstractMockEc2Instance.class.getDeclaredField(MAX_SHUTDOWN_TIME_MILLS), 0);
        AbstractMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();

        defaultMockEc2Instance.setInstanceType(InstanceType.C1_MEDIUM);
        defaultMockEc2Instance.setImageId(AMI_234);
        defaultMockEc2Instance.start();
        defaultMockEc2Instance.stop();

        defaultMockEc2Instance.initializeInternalTimer();

        Thread.sleep(TIMER_SLEEP_MILLIS);

        defaultMockEc2Instance.destroyInternalTimer();

        // reset the value
        long resetValue = Whitebox.invokeMethod(defaultMockEc2Instance, "getMsFromProperty", Constants.PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME,
                Constants.PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS);

        setFinalStatic(AbstractMockEc2Instance.class.getDeclaredField(MAX_SHUTDOWN_TIME_MILLS), resetValue);

        Assert.assertFalse(defaultMockEc2Instance.isStopping());
        Assert.assertFalse(defaultMockEc2Instance.isRunning());
        Assert.assertNull(defaultMockEc2Instance.getPubDns());
    }

    @Test
    public void Test_securityGroups(){

        AbstractMockEc2Instance defaultMockEc2Instance = new DefaultMockEc2Instance();
        defaultMockEc2Instance.setSecurityGroups(null); // simply for branch coverage

        Set<String> securityGroups = new HashSet<String>();
        securityGroups.add("sec-1");
        securityGroups.add("sec-2");

        defaultMockEc2Instance.setSecurityGroups(securityGroups);

        Assert.assertTrue(defaultMockEc2Instance.getSecurityGroups()== securityGroups);
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }



}
