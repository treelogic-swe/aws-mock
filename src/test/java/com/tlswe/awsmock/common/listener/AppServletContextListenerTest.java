package com.tlswe.awsmock.common.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.util.PersistenceUtils;
import com.tlswe.awsmock.ec2.control.MockEc2Controller;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEc2Controller.class, ServletContextEvent.class, PersistenceUtils.class,
        AbstractMockEc2Instance.class })
public class AppServletContextListenerTest {

    AppServletContextListener acl;

    @Mock
    ServletContextEvent sce;

    @Mock
    MockEc2Controller mockEc2Controller;

    @Mock
    AbstractMockEc2Instance mockEc2Instance;

    AbstractMockEc2Instance[] abstractMockEc2Instance = new AbstractMockEc2Instance[] {
            mockEc2Instance };

    @Before
    public void doInitialize() {

        List<AbstractMockEc2Instance> listOfMockedEc2s = new ArrayList<AbstractMockEc2Instance>();
        listOfMockedEc2s.add(mockEc2Instance);

        PowerMockito.mockStatic(MockEc2Controller.class);
        PowerMockito.mockStatic(PersistenceUtils.class);

        Mockito.when(MockEc2Controller.getInstance()).thenReturn(mockEc2Controller);
        Mockito.when(MockEc2Controller.getInstance().getAllMockEc2Instances())
                .thenReturn(listOfMockedEc2s);
        Mockito.when(PersistenceUtils.loadAll()).thenReturn(abstractMockEc2Instance);

        acl = new AppServletContextListener();
    }

    @Test
    public void Test_contextInitializedPersistenceDisabled() {
        acl.contextInitialized(sce);
    }

    @Test
    public void Test_contextInitializedPersistenceEnabledButLoadNull() {
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", true);
        Mockito.when(PersistenceUtils.loadAll()).thenReturn(null); // return null when loading array
        acl.contextInitialized(sce);
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", false);
    }

    @Test
    public void Test_contextInitializedPersistenceEnabled() {
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", true);
        acl.contextInitialized(sce);
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", false);
    }

    @Test
    public void Test_contextDestroyedPersistenceDisabled() {
        acl.contextDestroyed(sce);
    }

    @Test
    public void Test_contextDestroyedPersistenceEnabled() {
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", true);
        acl.contextDestroyed(sce);
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", false);
    }

}
