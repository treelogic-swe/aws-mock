package com.tlswe.awsmock.common.listener;

import com.tlswe.awsmock.common.util.PersistenceUtils;
import com.tlswe.awsmock.common.util.PersistenceUtils.PersistenceStoreType;
import com.tlswe.awsmock.ec2.control.*;
import com.tlswe.awsmock.ec2.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.servlet.ServletContextEvent;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockEc2Controller.class, MockVpcController.class, MockVolumeController.class, 
	MockTagsController.class, MockSubnetController.class, MockRouteTableController.class
	, MockInternetGatewayController.class, ServletContextEvent.class, PersistenceUtils.class,
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

    @Mock
    MockVpcController mockVpcController;

    @Mock
    MockVpc mockVpc;

    MockVpc[] mockVpcs = new MockVpc[] {
    		mockVpc };
    
    @Mock
    MockVolumeController mockVolumeController;

    @Mock
    MockVolume mockVolume;

    MockVolume[] mockVolumes = new MockVolume[] {
    		mockVolume };
    
    @Mock
    MockTagsController mockTagsController;

    @Mock
    MockTags mockTags;

    MockTags[] mockTagss = new MockTags[] {
    		mockTags };
    
    @Mock
    MockSubnetController mockSubnetController;

    @Mock
    MockSubnet mockSubnet;

    MockSubnet[] mockSubnets = new MockSubnet[] {
    		mockSubnet };

    @Mock
    MockRouteTableController mockRouteTableController;

    @Mock
    MockRouteTable mockRouteTable;

    MockRouteTable[] mockRouteTables = new MockRouteTable[] {
    		mockRouteTable };
    
    @Mock
    MockInternetGatewayController mockInternetGatewayController;

    @Mock
    MockInternetGateway mockInternetGateway;

    MockInternetGateway[] mockInternetGateways = new MockInternetGateway[] {
    		mockInternetGateway };
    
    @Before
    public void doInitialize() {

        List<AbstractMockEc2Instance> listOfMockedEc2s = new ArrayList<AbstractMockEc2Instance>();
        listOfMockedEc2s.add(mockEc2Instance);

        PowerMockito.mockStatic(MockEc2Controller.class);
        PowerMockito.mockStatic(PersistenceUtils.class);

        Mockito.when(MockEc2Controller.getInstance()).thenReturn(mockEc2Controller);
        Mockito.when(MockEc2Controller.getInstance().getAllMockEc2Instances())
                .thenReturn(listOfMockedEc2s);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.EC2)).thenReturn(abstractMockEc2Instance);

        
        List<MockVpc> listOfMockedVpc = new ArrayList<MockVpc>();
        listOfMockedVpc.add(mockVpc);

        PowerMockito.mockStatic(MockVpcController.class);
        
        Mockito.when(MockVpcController.getInstance()).thenReturn(mockVpcController);
        Mockito.when(MockVpcController.getInstance().describeVpcs())
                .thenReturn(listOfMockedVpc);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.VPC)).thenReturn(mockVpcs);

        List<MockVolume> listOfMockedVolume = new ArrayList<MockVolume>();
        listOfMockedVolume.add(mockVolume);

        PowerMockito.mockStatic(MockVolumeController.class);
        
        Mockito.when(MockVolumeController.getInstance()).thenReturn(mockVolumeController);
        Mockito.when(MockVolumeController.getInstance().describeVolumes())
                .thenReturn(listOfMockedVolume);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME)).thenReturn(mockVolumes);
        
        List<MockTags> listOfMockedTags = new ArrayList<MockTags>();
        listOfMockedTags.add(mockTags);

        PowerMockito.mockStatic(MockTagsController.class);
        
        Mockito.when(MockTagsController.getInstance()).thenReturn(mockTagsController);
        Mockito.when(MockTagsController.getInstance().describeTags())
                .thenReturn(listOfMockedTags);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.TAGS)).thenReturn(mockTagss);
        
        List<MockSubnet> listOfMockedSubnet = new ArrayList<MockSubnet>();
        listOfMockedSubnet.add(mockSubnet);

        PowerMockito.mockStatic(MockSubnetController.class);
        
        Mockito.when(MockSubnetController.getInstance()).thenReturn(mockSubnetController);
        Mockito.when(MockSubnetController.getInstance().describeSubnets())
                .thenReturn(listOfMockedSubnet);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.SUBNET)).thenReturn(mockSubnets);
        
        
        List<MockRouteTable> listOfMockedRouteTable = new ArrayList<MockRouteTable>();
        listOfMockedRouteTable.add(mockRouteTable);

        PowerMockito.mockStatic(MockRouteTableController.class);
        
        Mockito.when(MockRouteTableController.getInstance()).thenReturn(mockRouteTableController);
        Mockito.when(MockRouteTableController.getInstance().describeRouteTables())
                .thenReturn(listOfMockedRouteTable);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.ROUTETABLE)).thenReturn(mockRouteTables);
        
        List<MockInternetGateway> listOfMockedInternetGateway = new ArrayList<MockInternetGateway>();
        listOfMockedInternetGateway.add(mockInternetGateway);

        PowerMockito.mockStatic(MockInternetGatewayController.class);
        
        Mockito.when(MockInternetGatewayController.getInstance()).thenReturn(mockInternetGatewayController);
        Mockito.when(MockInternetGatewayController.getInstance().describeInternetGateways())
                .thenReturn(listOfMockedInternetGateway);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.INTERNETGATEWAY)).thenReturn(mockInternetGateways);
        
        
        acl = new AppServletContextListener();
    }

    @Test
    public void Test_contextInitializedPersistenceDisabled() {
        acl.contextInitialized(sce);
    }

    @Test
    public void Test_contextInitializedPersistenceEnabledButLoadNull() {
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", true);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.EC2)).thenReturn(null); // return null when loading array
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.VPC)).thenReturn(null); // return null when loading array
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME)).thenReturn(null);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.SUBNET)).thenReturn(null);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.ROUTETABLE)).thenReturn(null);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.INTERNETGATEWAY)).thenReturn(null);
        Mockito.when(PersistenceUtils.loadAll(PersistenceStoreType.TAGS)).thenReturn(null);
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

        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new AbstractMockEc2Instance[1]).getClass()), eq(PersistenceStoreType.EC2));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockVpc[1]).getClass()), eq(PersistenceStoreType.VPC));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockVolume[1]).getClass()), eq(PersistenceStoreType.VOLUME));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockTags[1]).getClass()), eq(PersistenceStoreType.TAGS));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockSubnet[1]).getClass()), eq(PersistenceStoreType.SUBNET));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockRouteTable[1]).getClass()), eq(PersistenceStoreType.ROUTETABLE));
        verifyStatic(PersistenceUtils.class);
        PersistenceUtils.saveAll(isA((new MockInternetGateway[1]).getClass()), eq(PersistenceStoreType.INTERNETGATEWAY));
        PowerMockito.verifyNoMoreInteractions(PersistenceUtils.class);
        
        Whitebox.setInternalState(AppServletContextListener.class, "persistenceEnabled", false);
    }

}
