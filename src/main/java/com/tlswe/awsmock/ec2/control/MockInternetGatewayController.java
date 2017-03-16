package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockInternetGateway;
import com.tlswe.awsmock.ec2.model.MockInternetGatewayAttachmentType;

/**
 * Factory class providing static methods for managing life cycle of mock Internet gateway. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock InternetGateway. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockInternetGatewayController {

    /**
     * Singleton instance of MockInternetGatewayController.
     */
    private static MockInternetGatewayController singletonMockInternetGatewayController = null;

    /**
     * Length of generated postfix of Internetgateway ID.
     */
    protected static final short INTERNETGATEWAY_ID_POSTFIX_LENGTH = 8;

    /**
     * A map of all the mock InternetGateway, id as key and {@link MockInternetGateway} as value.
     */
    private final Map<String, MockInternetGateway> allMockInternetGateways
        = new ConcurrentHashMap<String, MockInternetGateway>();

    /**
     * Constructor of MockInternetGatewayController is made private and only called once by {@link #getInstance()}.
     */
    private MockInternetGatewayController() {

    }

    /**
     *
     * @return singleton instance of {@link MockInternetGatewayController}
     */
    public static MockInternetGatewayController getInstance() {
        if (null == singletonMockInternetGatewayController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockInternetGatewayController.class) {
                if (null == singletonMockInternetGatewayController) {
                    singletonMockInternetGatewayController = new MockInternetGatewayController();
                }
            }
        }
        return singletonMockInternetGatewayController;
    }

    /**
     * List mock InternetGateway instances in current aws-mock.
     *
     * @return a collection all of {@link MockInternetGateway} .
     */
    public Collection<MockInternetGateway> describeInternetGateways() {
        return allMockInternetGateways.values();
    }

    /**
    * Create the mock InternetGateway.
    * @return mock InternetGateway.
    */
    public MockInternetGateway createInternetGateway() {

        MockInternetGateway ret = new MockInternetGateway();
        ret.setInternetGatewayId("InternetGateway-"
                + UUID.randomUUID().toString().substring(0, INTERNETGATEWAY_ID_POSTFIX_LENGTH));

        allMockInternetGateways.put(ret.getInternetGatewayId(), ret);
        return ret;
    }

    /**
     * Attach the mock InternetGateway.
     * @param vpcId vpc Id for InternetGateway.
     * @param internetgatewayId
     *            internetgatewayId to be deleted
     * @return mock InternetGateway.
     */
    public MockInternetGateway attachInternetGateway(final String internetgatewayId,
            final String vpcId) {

        MockInternetGateway ret = allMockInternetGateways.get(internetgatewayId);
        if (ret != null) {
            MockInternetGatewayAttachmentType internetGatewayAttachmentType = new MockInternetGatewayAttachmentType();
            internetGatewayAttachmentType.setVpcId(vpcId);
            internetGatewayAttachmentType.setState("Available");
            List<MockInternetGatewayAttachmentType> internetGatewayAttachmentSet
                = new ArrayList<MockInternetGatewayAttachmentType>();
            internetGatewayAttachmentSet.add(internetGatewayAttachmentType);
            ret.setAttachmentSet(internetGatewayAttachmentSet);
            allMockInternetGateways.put(ret.getInternetGatewayId(), ret);
            }

        return ret;
    }

    /**
     * Delete InternetGateway.
     *
     * @param internetgatewayId
     *            internetgatewayId to be deleted
     * @return Mock InternetGateway.
     */
    public MockInternetGateway deleteInternetGateway(final String internetgatewayId) {
        if (internetgatewayId != null && allMockInternetGateways.containsKey(internetgatewayId))
        {
            return allMockInternetGateways.remove(internetgatewayId);
        }

        return null;
    }

    /**
     * Clear {@link #allMockInternetGateways} and restore it from given a collection of instances.
     *
     * @param internetGateways
     *            collection of MockInternetGateway to restore
     */
    public void restoreAllInternetGateway(final Collection<MockInternetGateway> internetGateways) {
        allMockInternetGateways.clear();
        if (null != internetGateways) {
            for (MockInternetGateway instance : internetGateways) {
                allMockInternetGateways.put(instance.getInternetGatewayId(), instance);
            }
        }
    }
}
