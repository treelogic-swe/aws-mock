
package com.tlswe.awsmock.ec2.control;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockVpc;

/**
 * Factory class providing static methods for managing life cycle of mock Vpc instances. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock Vpc instances only. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockVpcController {

    /**
     * Singleton instance of MockVpcController.
     */
    private static MockVpcController singletonMockVpcController = null;

    /**
     * Length of generated postfix of Vpc ID.
     */
    protected static final short VPC_ID_POSTFIX_LENGTH = 8;

    /**
     * A map of all the mock VPC instances, instanceID as key and {@link MockVpc} as value.
     */
    private final Map<String, MockVpc> allMockVpcInstances = new ConcurrentHashMap<String, MockVpc>();

    /**
     * Constructor of MockVpcController is made private and only called once by {@link #getInstance()}.
     */
    private MockVpcController() {

    }

    /**
     *
     * @return singleton instance of {@link MockVpcController}
     */
    public static MockVpcController getInstance() {
        if (null == singletonMockVpcController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockVpcController.class) {
                if (null == singletonMockVpcController) {
                    singletonMockVpcController = new MockVpcController();
                }
            }
        }
        return singletonMockVpcController;
    }

    /**
     * List mock Vpc instances in current aws-mock.
     *
     * @return a collection of {@link MockVpcController} with specified instance IDs, or all of the mock vpc.
     */
    public Collection<MockVpc> describeVpcs() {
        return allMockVpcInstances.values();
    }

    /**
    * Create the mock VPC.
    * @param cidrBlock VPC cidr block.
    * @param instanceTenancy VPC instance tenancy.
    * @return mock VPC.
    */
    public MockVpc createVpc(
            final String cidrBlock, final String instanceTenancy) {

        MockVpc ret = new MockVpc();
        ret.setCidrBlock(cidrBlock);
        ret.setVpcId("vpc-" + UUID.randomUUID().toString().substring(0, VPC_ID_POSTFIX_LENGTH));
        ret.setInstanceTenancy(instanceTenancy);
        // Make sure only one VPC is default.
        if (allMockVpcInstances.size() == 0) {
            ret.setIsDefault(true);
        } else {
            ret.setIsDefault(false);
        }

        allMockVpcInstances.put(ret.getVpcId(), ret);
        return ret;
    }

    /**
     * Delete vpc.
     *
     * @param vpcId
     *            vpcId to be deleted
     * @return Mock vpc.
     */
    public MockVpc deleteVpc(final String vpcId) {
       if (vpcId != null && allMockVpcInstances.containsKey(vpcId)) {
           return allMockVpcInstances.remove(vpcId);
       }

       return null;
    }

    /**
     * List all mock vpc  within aws-mock.
     *
     * @return a collection of all the mock vpc
     */
    public Collection<MockVpc> getAllMockVpcInstances() {
        return allMockVpcInstances.values();
    }

    /**
     * Get mock vpc instance by vpc ID.
     *
     * @param vpcId
     *            ID of the mock vpc to get
     * @return the mock vpc object
     */
    public MockVpc getMockVpc(final String vpcId) {
        return allMockVpcInstances.get(vpcId);
    }
}
