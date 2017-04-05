package com.tlswe.awsmock.common.listener;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PersistenceUtils;
import com.tlswe.awsmock.common.util.PersistenceUtils.PersistenceStoreType;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.control.MockEc2Controller;
import com.tlswe.awsmock.ec2.control.MockInternetGatewayController;
import com.tlswe.awsmock.ec2.control.MockRouteTableController;
import com.tlswe.awsmock.ec2.control.MockSubnetController;
import com.tlswe.awsmock.ec2.control.MockTagsController;
import com.tlswe.awsmock.ec2.control.MockVolumeController;
import com.tlswe.awsmock.ec2.control.MockVpcController;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;
import com.tlswe.awsmock.ec2.model.MockInternetGateway;
import com.tlswe.awsmock.ec2.model.MockRouteTable;
import com.tlswe.awsmock.ec2.model.MockSubnet;
import com.tlswe.awsmock.ec2.model.MockTags;
import com.tlswe.awsmock.ec2.model.MockVolume;
import com.tlswe.awsmock.ec2.model.MockVpc;

/**
 * A ServletContextListener that does initializing tasks on event that context started (e.g. load and restore persistent
 * runtime object) and finalizing on event that context destroyed (e.g. save runtime objects to persistence).
 *
 * @author xma
 *
 */
public class AppServletContextListener implements ServletContextListener {

    /**
     * Log writer for this class.
     */
    private final Logger log = org.slf4j.LoggerFactory.getLogger(AppServletContextListener.class);

    /**
     * Global switch for persistence.
     */
    private static boolean persistenceEnabled = Boolean
            .parseBoolean(PropertiesUtils.getProperty(Constants.PROP_NAME_PERSISTENCE_ENABLED));

    /**
     * Period of cleaning up terminated mock ec2 instances.
     */
    private static int cleanupTerminatedInstancesPeriod = PropertiesUtils
            .getIntFromProperty(Constants.PROP_NAME_EC2_CLEANUP_TERMINATED_INSTANCES_TIME_SECONDS);

    /**
     * Millisecs in a second.
     */
    private static final long MILLISECS_IN_A_SECOND = 1000L;

    /**
     * Default constructor.
     */
    public AppServletContextListener() {

    }

    /**
     * We load the saved instances if persistence of enabled, on web application starting.
     *
     * @param sce
     *            the context event object
     */
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        if (persistenceEnabled) {
            // Load ec2 instances
            AbstractMockEc2Instance[] instanceArray = (AbstractMockEc2Instance[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.EC2);
            if (null != instanceArray) {
                MockEc2Controller.getInstance()
                        .restoreAllMockEc2Instances(Arrays.asList(instanceArray));
            }

         // Load Vpc
            MockVpc [] vpcArray = (MockVpc[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.VPC);
            if (null != vpcArray) {
                MockVpcController.getInstance()
                        .restoreAllMockVpc(Arrays.asList(vpcArray));
            }

            // Load Volume
            MockVolume [] volumeArray = (MockVolume[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.VOLUME);
            if (null != volumeArray) {
                MockVolumeController.getInstance()
                        .restoreAllMockVolume(Arrays.asList(volumeArray));
            }

            // Load Tags
            MockTags [] tagsArray = (MockTags[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.TAGS);
            if (null != tagsArray) {
                MockTagsController.getInstance()
                        .restoreAllMockTags(Arrays.asList(tagsArray));
            }

            // Load Subnet
            MockSubnet [] subnetArray = (MockSubnet[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.SUBNET);
            if (null != subnetArray) {
                MockSubnetController.getInstance()
                        .restoreAllMockSubnet(Arrays.asList(subnetArray));
            }

            // Load RouteTable
            MockRouteTable [] routetableArray = (MockRouteTable[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.ROUTETABLE);
            if (null != routetableArray) {
                MockRouteTableController.getInstance()
                        .restoreAllMockRouteTable(Arrays.asList(routetableArray));
            }

            // Load Internet Gateway
            MockInternetGateway [] internetgatewayArray = (MockInternetGateway[]) PersistenceUtils
                    .loadAll(PersistenceStoreType.INTERNETGATEWAY);
            if (null != internetgatewayArray) {
                MockInternetGatewayController.getInstance()
                        .restoreAllInternetGateway(Arrays.asList(internetgatewayArray));
            }
        }

        // start a timer for cleaning up terminated instances
        MockEc2Controller.getInstance()
                .cleanupTerminatedInstances(cleanupTerminatedInstancesPeriod);

        log.info("aws-mock started.");
    }

    /**
     * We save the instances if persistence of enabled, on web application shutting-down.
     *
     * @param sce
     *            the context event object
     */
    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {

        if (persistenceEnabled) {
            Collection<AbstractMockEc2Instance> instances = MockEc2Controller.getInstance()
                    .getAllMockEc2Instances();

            for (AbstractMockEc2Instance instance : instances) {
                // cancel and destroy the internal timers for all instances on
                // web app stopping
                instance.destroyInternalTimer();
            }
            // put all instances into an array which is serializable and type-cast safe for persistence
            AbstractMockEc2Instance[] array = new AbstractMockEc2Instance[instances.size()];
            instances.toArray(array);
            PersistenceUtils.saveAll(array, PersistenceStoreType.EC2);


            Collection<MockVpc> vpcs = MockVpcController.getInstance()
                    .describeVpcs();
            MockVpc[] vpcArray = new MockVpc[vpcs.size()];
            vpcs.toArray(vpcArray);
            PersistenceUtils.saveAll(vpcArray, PersistenceStoreType.VPC);

            Collection<MockVolume> volumes = MockVolumeController.getInstance()
                    .describeVolumes();
            MockVolume[] volumeArray = new MockVolume[volumes.size()];
            volumes.toArray(volumeArray);
            PersistenceUtils.saveAll(volumeArray, PersistenceStoreType.VPC);

            Collection<MockTags> tags = MockTagsController.getInstance().describeTags();

            MockTags[] tagArray = new MockTags[tags.size()];
            tags.toArray(tagArray);
            PersistenceUtils.saveAll(tagArray, PersistenceStoreType.TAGS);

            Collection<MockSubnet> subnets = MockSubnetController.getInstance().describeSubnets();

            MockSubnet[] subnetArray = new MockSubnet[subnets.size()];
            subnets.toArray(subnetArray);
            PersistenceUtils.saveAll(subnetArray, PersistenceStoreType.SUBNET);

            Collection<MockRouteTable> routetables = MockRouteTableController.getInstance().describeRouteTables();

            MockRouteTable[] routetableArray = new MockRouteTable[routetables.size()];
            routetables.toArray(routetableArray);
            PersistenceUtils.saveAll(routetableArray, PersistenceStoreType.ROUTETABLE);

            Collection<MockInternetGateway> internetgateways = MockInternetGatewayController.getInstance()
                    .describeInternetGateways();

            MockInternetGateway[] internetgatewayArray = new MockInternetGateway[internetgateways.size()];
            internetgateways.toArray(internetgatewayArray);
            PersistenceUtils.saveAll(internetgatewayArray, PersistenceStoreType.INTERNETGATEWAY);
        }

        MockEc2Controller.getInstance().destroyCleanupTerminatedInstanceTimer();

        log.info("aws-mock stopped.");
    }
}
