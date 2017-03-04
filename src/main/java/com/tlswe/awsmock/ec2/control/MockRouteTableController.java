package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockRoute;
import com.tlswe.awsmock.ec2.model.MockRouteTable;
import com.tlswe.awsmock.ec2.model.MockVpc;

/**
 * Factory class providing static methods for managing life cycle of mock RouteTable. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock RouteTable. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockRouteTableController {

    /**
     * Singleton instance of MockRouteTableController.
     */
    private static MockRouteTableController singletonMockRouteTableController = null;

    /**
     * Length of generated postfix of Route Table ID.
     */
    protected static final short ROUTETABLE_ID_POSTFIX_LENGTH = 8;

    /**
     * A map of all the mock VPC instances, instanceID as key and {@link MockVpc} as value.
     */
    private final Map<String, MockRouteTable> allMockRouteTables = new ConcurrentHashMap<String, MockRouteTable>();

    /**
     * Constructor of MockRouteTableController is made private and only called once by {@link #getInstance()}.
     */
    private MockRouteTableController() {

    }

    /**
     *
     * @return singleton instance of {@link MockVpcController}
     */
    public static MockRouteTableController getInstance() {
        if (null == singletonMockRouteTableController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockRouteTableController.class) {
                if (null == singletonMockRouteTableController) {
                    singletonMockRouteTableController = new MockRouteTableController();
                }
            }
        }
        return singletonMockRouteTableController;
    }

    /**
     * List mock RouteTable instances in current aws-mock.
     *
     * @return a collection all of {@link MockRouteTable} .
     */
    public Collection<MockRouteTable> describeRouteTables() {
        return allMockRouteTables.values();
    }

    /**
    * Create the mock RouteTable.
    * @param cidrBlock VPC cidr block.
    * @param vpcId vpc Id for RouteTable.
    * @return mock RouteTable.
    */
    public MockRouteTable createRouteTable(
            final String cidrBlock, final String vpcId) {

        MockRouteTable ret = new MockRouteTable();
        ret.setRouteTableId(
                "rtb-" + UUID.randomUUID().toString().substring(0, ROUTETABLE_ID_POSTFIX_LENGTH));
        ret.setVpcId(vpcId);
        MockRoute mockRoute = new MockRoute();
        mockRoute.setGatewayId("local");
        mockRoute.setDestinationCidrBlock(cidrBlock);
        mockRoute.setState("active");
        mockRoute.setOrigin("CreateRouteTable");
        List<MockRoute> routeSet = new ArrayList<MockRoute>();
        routeSet.add(mockRoute);
        ret.setRouteSet(routeSet);

        allMockRouteTables.put(ret.getRouteTableId(), ret);
        return ret;
    }

    /**
     * Delete RouteTable.
     *
     * @param routetableId
     *            RouteTableId to be deleted
     * @return Mock RouteTable.
     */
    public MockRouteTable deleteRouteTable(final String routetableId) {

        if (routetableId != null && allMockRouteTables.containsKey(routetableId)) {
            return allMockRouteTables.remove(routetableId);
        }
        return null;
    }

    /**
     * Create the mock Route.
     * @param destinationCidrBlock : Route destinationCidrBlock.
     * @param internetGatewayId : for gateway Id.
     * @param routeTableId : for Route.
     * @return mock Route.
     */
    public MockRoute createRoute(
            final String destinationCidrBlock, final String internetGatewayId,
            final String routeTableId) {

        MockRoute ret = new MockRoute();
        ret.setDestinationCidrBlock(destinationCidrBlock);
        ret.setGatewayId(internetGatewayId);
        ret.setOrigin("CreateRoute");

        MockRouteTable mockRouteTable = getMockRouteTable(routeTableId);
        mockRouteTable.getRouteSet().add(ret);
        return ret;
    }

    /**
     * Get mock RouteTable instance by RouteTable ID.
     *
     * @param routetableId
     *            ID of the mock RouteTable to get
     * @return the mock RouteTable object
     */
    public MockRouteTable getMockRouteTable(final String routetableId) {
        return allMockRouteTables.get(routetableId);
    }
}
