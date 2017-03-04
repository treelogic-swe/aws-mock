package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;

/**
 * The Class MockRoute.
 */
public class MockRoute implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The destination cidr block. */
    private String destinationCidrBlock = null;

    /** The gateway id. */
    private String gatewayId = null;

    /** The instance id. */
    private String instanceId = null;

    /** The instance owner id. */
    private String instanceOwnerId = null;

    /** The network interface id. */
    private String networkInterfaceId = null;

    /** The vpc peering connection id. */
    private String vpcPeeringConnectionId = null;

    /** The state. */
    private String state = null;

    /** The origin. */
    private String origin = null;

    /**
    * Gets the value of the destinationCidrBlock property.
    *
    * @return
    *     possible object is
    *     {@link String }
    *
    */
    public final String getDestinationCidrBlock() {
        return destinationCidrBlock;
    }

    /**
     * Sets the value of the destinationCidrBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setDestinationCidrBlock(final String value) {
        this.destinationCidrBlock = value;
    }

    /**
     * Gets the value of the gatewayId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getGatewayId() {
        return gatewayId;
    }

    /**
     * Sets the value of the gatewayId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setGatewayId(final String value) {
        this.gatewayId = value;
    }

    /**
     * Gets the value of the instanceId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the value of the instanceId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setInstanceId(final String value) {
        this.instanceId = value;
    }

    /**
     * Gets the value of the networkInterfaceId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getNetworkInterfaceId() {
        return networkInterfaceId;
    }

    /**
     * Sets the value of the networkInterfaceId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setNetworkInterfaceId(final String value) {
        this.networkInterfaceId = value;
    }

    /**
     * Gets the value of the vpcPeeringConnectionId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getVpcPeeringConnectionId() {
        return vpcPeeringConnectionId;
    }

    /**
     * Sets the value of the vpcPeeringConnectionId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setVpcPeeringConnectionId(final String value) {
        this.vpcPeeringConnectionId = value;
    }

    /**
     * Gets the instance owner id.
     *
     * @return the instance owner id
     */
    public final String getInstanceOwnerId() {
        return instanceOwnerId;
    }

    /**
     * Sets the instance owner id.
     *
     * @param newinstanceOwnerId the new instance owner id
     */
    public final void setInstanceOwnerId(final String newinstanceOwnerId) {
        this.instanceOwnerId = newinstanceOwnerId;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public final String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param newstate the new state
     */
    public final void setState(final String newstate) {
        this.state = newstate;
    }

    /**
     * Gets the origin.
     *
     * @return the origin
     */
    public final String getOrigin() {
        return origin;
    }

    /**
     * Sets the origin.
     *
     * @param neworigin the new origin
     */
    public final void setOrigin(final String neworigin) {
        this.origin = neworigin;
    }
}
