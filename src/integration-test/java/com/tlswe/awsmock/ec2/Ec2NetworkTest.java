/**
 * File name: Ec2NetworkTest.java Author: Jiaqi Chen Create date: Jul 1,
 * 2016
 */
package com.tlswe.awsmock.ec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagDescription;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.InternetGateway;

/**
 * This test class covers some network related APIs test cases in aws-mock.
 *
 * @author Jiaqi Chen
 */
public class Ec2NetworkTest extends BaseTest {

    /**
     * Prefined Mock EC2 Placement
     */
    private static final String MOCK_EC2_PLACEMENT = PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_PLACEMENT);
    
    /**
     * Predefined mock cidr block.
     */
    private static final String MOCK_CIDR_BLOCK = PropertiesUtils
            .getProperty(Constants.PROP_NAME_CIDR_BLOCK);
    
    /**
     * Property key for InstanceTenacy.
     */
    private static final String PROPERTY_TENANCY = "Default";
    
    /**
     * 2 minutes timeout.
     */
    private static final int TIMEOUT_LEVEL1 = 120000;

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(Ec2NetworkTest.class);

    /**
     * Test describing vpcs.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeVpcsTest() {
        log.info("Start describing vpcs test");
        createVpcTest();
        List<Vpc> vpcs = describeVpcs();

        Assert.assertNotNull("vpcs should not be null", vpcs);
        Assert.assertNotNull("vpc id should not be null", vpcs.get(0).getVpcId());
        
        Assert.assertTrue("Vpc Should be deleted", deleteVpc(vpcs.get(0).getVpcId()));
    }

    /**
     * Test create vpcs.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createVpcTest() {
        log.info("Start describing vpcs test");

        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);

        Assert.assertNotNull("vpcs should not be null", vpc);
        Assert.assertNotNull("vpc id should not be null", vpc.getVpcId());
    }
    
    /**
     * Delete create vpcs.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteVpcTest() {
        log.info("Start describing vpcs test");

        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);

        Assert.assertNotNull("vpcs should not be null", vpc);
        Assert.assertNotNull("vpc id should not be null", vpc.getVpcId());
        
        Assert.assertTrue("Vpc Should be deleted", deleteVpc(vpc.getVpcId()));
    }
    
    /**
     * Test describing security group.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeSecurityGroupTest() {
        log.info("Start describing security group test");
        createSecurityGroupTest();
        SecurityGroup securityGroup = getSecurityGroup();

        Assert.assertNotNull("security group should not be null", securityGroup);
        Assert.assertNotNull("group id should not be null", securityGroup.getGroupId());
        Assert.assertNotNull("vpc id should not be null", securityGroup.getVpcId());
    }

    /**
     * Test describing internet gateway.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeInternetGatewayTest() {
        log.info("Start describing internet gateway test");

        createInternetGatewayTest();
        InternetGateway internetGateway = getInternetGateway();

        Assert.assertNotNull("internet gateway should not be null", internetGateway);
        Assert.assertNotNull("internet gateway id should not be null",
                internetGateway.getInternetGatewayId());
        Assert.assertTrue("internet gateway should be deleted", deleteInternetGateway(internetGateway.getInternetGatewayId()));
    }

    /**
     * Test create internet gateway.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createInternetGatewayTest() {
        log.info("create internet gateway test");

        InternetGateway internetGateway = createInternetGateway();

        Assert.assertNotNull("internet gateway should not be null", internetGateway);
        Assert.assertNotNull("internet gateway id should not be null",
                internetGateway.getInternetGatewayId());
    }
    
    /**
     * Test attach internet gateway.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void attachInternetGatewayTest() {
        log.info("Attach internet gateway test");
        
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        InternetGateway internetGateway = createInternetGateway();
        
        Assert.assertTrue("internet gateway should be attached to vpc", attachInternetGateway(internetGateway.getInternetGatewayId(), vpc.getVpcId()));
    }
    
    /**
     * Test delete internet gateway.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteInternetGatewayTest() {
        log.info("delete internet gateway test");

        InternetGateway internetGateway = createInternetGateway();

        Assert.assertNotNull("internet gateway should not be null", internetGateway);
        Assert.assertNotNull("internet gateway id should not be null",
                internetGateway.getInternetGatewayId());
        Assert.assertTrue("internet gateway should be deleted", deleteInternetGateway(internetGateway.getInternetGatewayId()));
    }
    
    /**
     * Test describing route table.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeRouteTableTest() {
        log.info("Start describing route table test");
        createRouteTableTest();
        
        RouteTable routeTable = getRouteTable();

        Assert.assertNotNull("route table should not be null", routeTable);
        Assert.assertNotNull("route table id should not be null", routeTable.getRouteTableId());
        
        Assert.assertTrue("route table should be deleted", deleteRouteTable(routeTable.getRouteTableId()));
    }
    
    /**
     * Test create route table.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createRouteTableTest() {
        log.info("Start create route table test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        RouteTable routeTable = createRouteTable(vpc.getVpcId());
        
        Assert.assertNotNull("route table should not be null", routeTable);
        Assert.assertNotNull("route table id should not be null", routeTable.getRouteTableId());
    }
    
    /**
     * Test Delete route table.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteRouteTableTest() {
        log.info("Start delete route table test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);

        RouteTable routeTable = createRouteTable(vpc.getVpcId());

        Assert.assertNotNull("route table should not be null", routeTable);
        Assert.assertNotNull("route table id should not be null", routeTable.getRouteTableId());

        Assert.assertTrue("route table should be deleted", deleteRouteTable(routeTable.getRouteTableId()));
    }
    
    /**
     * Test describing Availability Zones.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeAvailabilityZonesTest() {
        log.info("Start describing Availability Zones test");

        AvailabilityZone availabiltyZone = getAvailiablityZones();

        Assert.assertNotNull("route table should not be null", availabiltyZone);
        Assert.assertNotNull("route table id should not be null", availabiltyZone.getZoneName());
    }

    /**
     * Test describing Subnets.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeSubnetTest() {
        log.info("Start describing Subnet test");
        createSubnetTest();
        Subnet subnet = getSubnet();

        Assert.assertNotNull("subnet should not be null", subnet);
        Assert.assertNotNull("subnet id should not be null", subnet.getSubnetId());
        Assert.assertTrue("subnet should be deleted", deleteSubnet(subnet.getSubnetId()));
    }

    /**
     * Test create Subnet.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createSubnetTest() {
        log.info("Start create Subnet test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        Subnet subnet = createSubnet(MOCK_CIDR_BLOCK, vpc.getVpcId());

        Assert.assertNotNull("subnet should not be null", subnet);
        Assert.assertNotNull("subnet id should not be null", subnet.getSubnetId());
    }
    
    /**
     * Test create Security Group.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createSecurityGroupTest() {
        log.info("Start create Security Group test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        String securityGroupId = createSecurityGroup("test-sg", "groupDescription", vpc.getVpcId());

        Assert.assertNotNull("Security Group id should not be null", securityGroupId);
    }

    /**
     * Test Authorize Security Group Ingress.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void authorizeSecurityGroupIngressTest() {
        log.info("Start authorizeSecurityGroupIngressTest test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        String securityGroupId = createSecurityGroup("test-sg", "groupDescription", vpc.getVpcId());

        Assert.assertNotNull("Security Group id id should not be null", securityGroupId);
        Assert.assertTrue("Security Group should be deleted", authorizeSecurityGroupIngress(securityGroupId, "TCP", 22, MOCK_CIDR_BLOCK));
        
    }

    /**
     * Test Authorize Security Group Egress.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void authorizeSecurityGroupEgressTest() {
        log.info("Start authorizeSecurityGroupEgressTest test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        String securityGroupId = createSecurityGroup("test-sg", "groupDescription", vpc.getVpcId());

        Assert.assertNotNull("Security Group id should not be null", securityGroupId);
        Assert.assertTrue("Security Group should be deleted", authorizeSecurityGroupEgress(securityGroupId, "TCP", 22, MOCK_CIDR_BLOCK));
        
    }
    
    /**
     * Test delete Subnet.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteSubnetTest() {
        log.info("Start delete Subnet test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        Subnet subnet = createSubnet(MOCK_CIDR_BLOCK, vpc.getVpcId());

        Assert.assertNotNull("subnet should not be null", subnet);
        Assert.assertNotNull("subnet id should not be null", subnet.getSubnetId());
        Assert.assertTrue("subnet should be deleted", deleteSubnet(subnet.getSubnetId()));
    }
    
    /**
     * Test delete SecurityGroup.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteSecurityGroupTest() {
        log.info("Start delete SecurityGroup test");
        Vpc vpc = createVpc(MOCK_CIDR_BLOCK, PROPERTY_TENANCY);
        
        String securityGroupId = createSecurityGroup("test-sg", "groupDescription", vpc.getVpcId());

        Assert.assertNotNull("Security Group should not be null", securityGroupId);
        Assert.assertTrue("Security Group should be deleted", deleteSecurityGroup(securityGroupId));
    }

    /**
     * Test describing Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeVolumesTest() {
        log.info("Start describing volume test");
        createVolumesTest();
        Volume volume = getVolume();

        Assert.assertNotNull("volume should not be null", volume);
        Assert.assertNotNull("volume id should not be null", volume.getVolumeId());
        Assert.assertTrue("volume should be deleted", deleteVolume(volume.getVolumeId()));
    }
    
    /**
     * Test describing Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeVolumesAllTest() {
        log.info("Start describing volume test");
        List<Volume>  volumes = getVolumes();
        Assert.assertNotNull("volume should not be null", volumes);
        Assert.assertNotNull("volume size", volumes.size());
        log.info("Sizes " + volumes.size());
    }

    /**
     * Test create Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createVolumesTest() {
        log.info("Start create volume test");
        
        Volume volume = createVolume(MOCK_EC2_PLACEMENT, 1000, 32, "snap-12312313", "gp2");

        Assert.assertNotNull("volume should not be null", volume);
        Assert.assertNotNull("volume id should not be null", volume.getVolumeId());
    }

    /**
     * Test create Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createVolumes100Test() {
        for(int i=0 ; i< 10; i++)
        {
            createVolumesTest();
        }
    }

    /**
     * Test describing vpcs.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeAllNetworksTest() {
        log.info("Start describing vpcs test");
        List<Vpc> vpcs = describeVpcs();

        Assert.assertNotNull("vpcs should not be null", vpcs);
        Assert.assertNotNull("vpc id should not be null", vpcs.get(0).getVpcId());
        log.info("Vpc Sizes " + vpcs.size());

        log.info("Start describing vpcs test");
        List<Subnet> subnets = getSubnets();

        Assert.assertNotNull("vpcs should not be null", subnets);
        Assert.assertNotNull("vpc id should not be null", subnets.get(0).getSubnetId());
        log.info("Subnets Sizes " + subnets.size());

        log.info("Start describing vpcs test");
        List<InternetGateway> internetGateways = getInternetGateways();

        Assert.assertNotNull("vpcs should not be null", internetGateways);
        Assert.assertNotNull("vpc id should not be null", internetGateways.get(0).getInternetGatewayId());
        log.info("Subnets Sizes " + internetGateways.size());

    }

    /**
     * Test create Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createNetworkResourcesTest() {
        
        //Create VPCs
        for(int i =0 ; i < 2 ; i++)
        {
            createVpcTest(); 
        }
        
        List<Vpc> vpcs = describeVpcs();
        
        // Create Subnet
        for(Vpc vpc : vpcs) {
            
            for(int j=0; j<2; j++)
            {
                Subnet subnet = createSubnet(MOCK_CIDR_BLOCK, vpc.getVpcId());
                RouteTable routeTable = createRouteTable(vpc.getVpcId());
                InternetGateway internetGateway = createInternetGateway();

                createRoute(routeTable.getRouteTableId(), internetGateway.getInternetGatewayId(), MOCK_CIDR_BLOCK);
                
                attachInternetGateway(internetGateway.getInternetGatewayId(), vpc.getVpcId());
            }
        }
    }

    /**
     * Test create Tags.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void createTagsTest() {
        log.info("Start create Tags test");
        Collection<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        
        Collection<Tag> tags = new ArrayList<Tag>();
        Tag tag1 = new Tag();
        tag1.setKey("tag1");
        tag1.setValue("value1");
        tags.add(tag1);
        
        Tag tag2 = new Tag();
        tag2.setKey("tag2");
        tag2.setValue("value2");
        tags.add(tag2);
        
        Assert.assertTrue("Tags should be created.",createTags(resources, tags));
       
    }
    
    /**
     * Test delete Tags.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteTagsTest() {
        log.info("Delete Tags test");
        Collection<String> resources = new ArrayList<String>();
        resources.add("resource1");
        resources.add("resource2");
        
        Collection<Tag> tags = new ArrayList<Tag>();
        Tag tag1 = new Tag();
        tag1.setKey("tag1");
        tag1.setValue("value1");
        tags.add(tag1);
        
        Tag tag2 = new Tag();
        tag2.setKey("tag2");
        tag2.setValue("value2");
        tags.add(tag2);
        
        Assert.assertTrue("Tags should be created.", deleteTags(resources, tags));
       
    }
    
    /**
     * Test delete Tags.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describerTagsTest() {
        log.info("Describe Tags test");
        createTagsTest();
        
        List<TagDescription> tagsDesc = getTags();
        Assert.assertNotNull("tag Desc should not be null", tagsDesc);
        
        Collection<String> resources = new ArrayList<String>();
        Collection<Tag> tags = new ArrayList<Tag>();
        
        for(TagDescription tagDesc : tagsDesc)
        {
            Tag tag = new Tag();
            tag.setKey(tagDesc.getKey());
            tag.setValue(tagDesc.getValue());
            tags.add(tag);
            
            resources.add(tagDesc.getResourceId());
        }
        
        Assert.assertTrue("Tags should be created.", deleteTags(resources, tags));
    }
    
    /**
     * Test delete Volumes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void deleteVolumesTest() {
        log.info("Start describing volume test");
        
        Volume volume = createVolume(MOCK_EC2_PLACEMENT, 1000, 32, "snap-12312313", "gp2");

        Assert.assertNotNull("volume should not be null", volume);
        Assert.assertNotNull("volume id should not be null", volume.getVolumeId());
        
        Assert.assertTrue("volume should be deleted",deleteVolume(volume.getVolumeId()));
    }
}
