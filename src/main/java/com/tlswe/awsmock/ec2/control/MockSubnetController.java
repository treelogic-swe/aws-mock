package com.tlswe.awsmock.ec2.control;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockSubnet;
import com.tlswe.awsmock.ec2.model.MockVpc;

/**
 * Factory class providing static methods for managing life cycle of mock Subnet. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock Subnet. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockSubnetController {

    /**
     * Singleton instance of MockSubnetController.
     */
    private static MockSubnetController singletonMockSubnetController = null;

    /**
     * Length of generated postfix of Subnet ID.
     */
    protected static final short SUBNET_ID_POSTFIX_LENGTH = 8;

    /**
     * A map of all the mock VPC instances, instanceID as key and {@link MockVpc} as value.
     */
    private final Map<String, MockSubnet> allMockSubnets = new ConcurrentHashMap<String, MockSubnet>();

    /**
     * Constructor of MockSubnetController is made private and only called once by {@link #getInstance()}.
     */
    private MockSubnetController() {

    }

    /**
     *
     * @return singleton instance of {@link MockVpcController}
     */
    public static MockSubnetController getInstance() {
        if (null == singletonMockSubnetController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockSubnetController.class) {
                if (null == singletonMockSubnetController) {
                    singletonMockSubnetController = new MockSubnetController();
                }
            }
        }
        return singletonMockSubnetController;
    }

    /**
     * List mock Subnet instances in current aws-mock.
     *
     * @return a collection all of {@link MockSubnet} .
     */
    public Collection<MockSubnet> describeSubnets() {
        return allMockSubnets.values();
    }

    /**
    * Create the mock Subnet.
    * @param cidrBlock VPC cidr block.
    * @param vpcId vpc Id for subnet.
    * @return mock Subnet.
    */
    public MockSubnet createSubnet(
            final String cidrBlock, final String vpcId) {

        MockSubnet ret = new MockSubnet();
        ret.setCidrBlock(cidrBlock);

        ret.setSubnetId(
                "subnet-" + UUID.randomUUID().toString().substring(0, SUBNET_ID_POSTFIX_LENGTH));
        ret.setVpcId(vpcId);

        allMockSubnets.put(ret.getSubnetId(), ret);
        return ret;
    }

    /**
     * Delete Subnet.
     *
     * @param subnetId
     *            subnetId to be deleted
     * @return Mock Subnet.
     */
    public MockSubnet deleteSubnet(final String subnetId) {

        if (subnetId != null && allMockSubnets.containsKey(subnetId)) {
            return allMockSubnets.remove(subnetId);
        }

        return null;
    }

    /**
     * Clear {@link #allMockSubnets} and restore it from given a collection of instances.
     *
     * @param subnets
     *            collection of MockSubnet to restore
     */
    public void restoreAllMockSubnet(final Collection<MockSubnet> subnets) {
        allMockSubnets.clear();
        if (null != subnets) {
            for (MockSubnet instance : subnets) {
                allMockSubnets.put(instance.getSubnetId(), instance);
            }
        }
    }
}
