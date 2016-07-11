/**
 * File name: Ec2NetworkTest.java Author: Jiaqi Chen Create date: Jul 1,
 * 2016
 */
package com.tlswe.awsmock.ec2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.InternetGateway;

/**
 * This test class covers some network related APIs test cases in aws-mock.
 *
 * @author Jiaqi Chen
 */
public class Ec2NetworkTest extends BaseTest {

    /**
     * 2 minutes timeout.
     */
    private static final int TIMEOUT_LEVEL1 = 120000;

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(Ec2EndpointTest.class);

    /**
     * Test describing vpcs.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeVpcsTest() {
        log.info("Start describing vpcs test");

        List<Vpc> vpcs = describeVpcs();

        Assert.assertNotNull("vpcs should not be null", vpcs);
        Assert.assertNotNull("vpc id should not be null", vpcs.get(0).getVpcId());
    }


    /**
     * Test describing security group.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeSecurityGroupTest() {
        log.info("Start describing security group test");

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

        InternetGateway internetGateway = getInternetGateway();

        Assert.assertNotNull("internet gateway should not be null", internetGateway);
        Assert.assertNotNull("internet gateway id should not be null", internetGateway.getInternetGatewayId());
    }


    /**
     * Test describing route table.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void describeRouteTableTest() {
        log.info("Start describing route table test");

        RouteTable routeTable = getRouteTable();

        Assert.assertNotNull("route table should not be null", routeTable);
        Assert.assertNotNull("route table id should not be null", routeTable.getRouteTableId());
    }
}
