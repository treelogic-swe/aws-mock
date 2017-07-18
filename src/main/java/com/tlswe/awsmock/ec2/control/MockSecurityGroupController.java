package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockIpPermissionType;
import com.tlswe.awsmock.ec2.model.MockSecurityGroup;
import com.tlswe.awsmock.ec2.model.MockSubnet;
import com.tlswe.awsmock.ec2.model.MockVpc;

/**
 * Factory class providing static methods for managing life cycle of mock Security. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock SecurityGroup. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockSecurityGroupController {

    /**
     * Singleton instance of MockSecurityGroupController.
     */
    private static MockSecurityGroupController singletonMockSecurityGroupController = null;

    /**
     * Length of generated postfix of SecurityGroup ID.
     */
    protected static final short SECURITYGROUP_ID_POSTFIX_LENGTH = 8;

    /**
     * A map of all the Mock SecurityGroup instances, instanceID as key and {@link MockSecurityGroup} as value.
     */
    private final Map<String, MockSecurityGroup> allMockSecurityGroup =
                   new ConcurrentHashMap<String, MockSecurityGroup>();

    /**
     * Constructor of MockSecurityGroupController is made private and only called once by {@link #getInstance()}.
     */
    private MockSecurityGroupController() {

    }

    /**
     *
     * @return singleton instance of {@link MockSecurityGroupController}
     */
    public static MockSecurityGroupController getInstance() {
        if (null == singletonMockSecurityGroupController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockSecurityGroupController.class) {
                if (null == singletonMockSecurityGroupController) {
                    singletonMockSecurityGroupController = new MockSecurityGroupController();
                }
            }
        }
        return singletonMockSecurityGroupController;
    }

    /**
     * List mock SecurityGroup instances in current aws-mock.
     *
     * @return a collection all of {@link MockSecurityGroup} .
     */
    public Collection<MockSecurityGroup> describeSecurityGroups() {
        return allMockSecurityGroup.values();
    }

    /**
    * Create the mock SecurityGroup.
    * @param groupName group Name.
    * @param groupDescription group Description.
    * @param vpcId vpc Id for Security Group.
    * @return mock Security Group.
    */
    public MockSecurityGroup createSecurityGroup(
            final String groupName, final String groupDescription, final String vpcId) {

        MockSecurityGroup ret = new MockSecurityGroup();
        ret.setGroupName(groupName);
        ret.setGroupDescription(groupDescription);
        ret.setGroupId(
                "sg-" + UUID.randomUUID().toString().substring(0, SECURITYGROUP_ID_POSTFIX_LENGTH));
        ret.setVpcId(vpcId);
        MockIpPermissionType mockIpPermissionType = new MockIpPermissionType();
        mockIpPermissionType.setIpProtocol("-1");
        List<String> ipRanges = new ArrayList<String>();
        ipRanges.add("0.0.0.0/0");
        mockIpPermissionType.setIpRanges(ipRanges);
        List<MockIpPermissionType> mockIpPermissionTypes = new ArrayList<MockIpPermissionType>();
        mockIpPermissionTypes.add(mockIpPermissionType);
        ret.setIpPermissionsEgress(mockIpPermissionTypes);
        List<MockIpPermissionType> mockIpPermissionTypesIngress = new ArrayList<MockIpPermissionType>();
        ret.setIpPermissions(mockIpPermissionTypesIngress);
        allMockSecurityGroup.put(ret.getGroupId(), ret);
        return ret;
    }

    /**
     * Authorize the mock SecurityGroup to Ingress IpProtocol.
     * @param groupId group Id.
     * @param ipProtocol ipProtocol Ingress.
     * @param fromPort fromPort for Security Group.
     * @param toPort toPort for Security Group.
     * @param cidrIp cidrIp for Ingress
     * @return mock Security Group.
     */
     public MockSecurityGroup authorizeSecurityGroupIngress(final String groupId, final String ipProtocol,
         final Integer fromPort, final Integer toPort, final String cidrIp) {
         if (groupId == null) {
             return null;
         }
         MockSecurityGroup ret = allMockSecurityGroup.get(groupId);
         if (ret != null) {
              MockIpPermissionType mockIpPermissionType = new MockIpPermissionType();
              mockIpPermissionType.setIpProtocol(ipProtocol);
              mockIpPermissionType.setFromPort(fromPort);
              mockIpPermissionType.setToPort(toPort);
              List<String> ipRanges = new ArrayList<String>();
              ipRanges.add(cidrIp);
              mockIpPermissionType.setIpRanges(ipRanges);
              List<MockIpPermissionType> mockIpPermissionTypes = ret.getIpPermissions();
              mockIpPermissionTypes.add(mockIpPermissionType);
              ret.setIpPermissions(mockIpPermissionTypes);
              allMockSecurityGroup.put(ret.getGroupId(), ret);
         }
         return ret;
     }

     /**
      * Authorize the mock SecurityGroup to Engress IpProtocol.
      * @param groupId group Id.
      * @param ipProtocol ipProtocol Engress.
      * @param fromPort fromPort for Security Group.
      * @param toPort toPort for Security Group.
      * @param cidrIp cidrIp for Engress
      * @return mock Security Group.
      */
      public MockSecurityGroup authorizeSecurityGroupEgress(final String groupId,
          final String ipProtocol, final Integer fromPort, final Integer toPort, final String cidrIp) {
          if (groupId == null) {
              return null;
          }
          MockSecurityGroup ret = allMockSecurityGroup.get(groupId);
          if (ret != null) {
               MockIpPermissionType mockIpPermissionType = new MockIpPermissionType();
               mockIpPermissionType.setIpProtocol(ipProtocol);
               mockIpPermissionType.setFromPort(fromPort);
               mockIpPermissionType.setToPort(toPort);
               List<String> ipRanges = new ArrayList<String>();
               ipRanges.add(cidrIp);
               mockIpPermissionType.setIpRanges(ipRanges);
               List<MockIpPermissionType> mockIpPermissionTypes = ret.getIpPermissionsEgress();
               mockIpPermissionTypes.add(mockIpPermissionType);
               ret.setIpPermissionsEgress(mockIpPermissionTypes);
               allMockSecurityGroup.put(ret.getGroupId(), ret);
          }
          return ret;
      }

    /**
     * Delete MockSecurityGroup.
     *
     * @param securityGroupId
     *            securityGroupId to be deleted
     * @return MockSecurityGroup.
     */
    public MockSecurityGroup deleteSecurityGroup(final String securityGroupId) {

        if (securityGroupId != null && allMockSecurityGroup.containsKey(securityGroupId)) {
            return allMockSecurityGroup.remove(securityGroupId);
        }

        return null;
    }

    /**
     * Clear {@link #allMockSecurityGroup} and restore it from given a collection of instances.
     *
     * @param securityGroups
     *            collection of MockSecurityGroup to restore
     */
    public void restoreAllMockSecurityGroup(final Collection<MockSecurityGroup> securityGroups) {
        allMockSecurityGroup.clear();
        if (null != securityGroups) {
            for (MockSecurityGroup instance : securityGroups) {
                allMockSecurityGroup.put(instance.getGroupId(), instance);
            }
        }
    }
}
