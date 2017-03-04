package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;

/**
 * The Class MockRouteTableAssoication.
 */
public class MockRouteTableAssoication implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The route table association id. */
    private String routeTableAssociationId = null;

    /** The route table id. */
    private String routeTableId = null;

    /** The subnet id. */
    private String subnetId = null;

    /** The main. */
    private Boolean main = false;

    /**
     * Gets the route table association id.
     *
     * @return the route table association id
     */
    public final String getRouteTableAssociationId() {
        return routeTableAssociationId;
    }

    /**
     * Sets the route table association id.
     *
     * @param newrouteTableAssociationId the new route table association id
     */
    public final void setRouteTableAssociationId(final String newrouteTableAssociationId) {
        this.routeTableAssociationId = newrouteTableAssociationId;
    }

    /**
     * Gets the route table id.
     *
     * @return the route table id
     */
    public final String getRouteTableId() {
        return routeTableId;
    }

    /**
     * Sets the route table id.
     *
     * @param newrouteTableId the new route table id
     */
    public final void setRouteTableId(final String newrouteTableId) {
        this.routeTableId = newrouteTableId;
    }

    /**
     * Gets the subnet id.
     *
     * @return the subnet id
     */
    public final String getSubnetId() {
        return subnetId;
    }

    /**
     * Sets the subnet id.
     *
     * @param newsubnetId the new subnet id
     */
    public final void setSubnetId(final String newsubnetId) {
        this.subnetId = newsubnetId;
    }

    /**
     * Gets the main.
     *
     * @return the main
     */
    public final Boolean getMain() {
        return main;
    }

    /**
     * Sets the main.
     *
     * @param newmain the new main
     */
    public final void setMain(final Boolean newmain) {
        this.main = newmain;
    }
}
