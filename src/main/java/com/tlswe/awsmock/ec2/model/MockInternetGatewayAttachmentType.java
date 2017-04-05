package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;

/**
 * The Class MockInternetGatewayAttachmentType.
 */
public class MockInternetGatewayAttachmentType implements Serializable {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /** The vpc id. */
    private String vpcId = null;

    /** The state. */
    private String state = null;

    /**
     * Gets the value of the vpcId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getVpcId() {
        return vpcId;
    }

    /**
     * Sets the value of the vpcId property.
     *
     * @param newvalue
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setVpcId(final String newvalue) {
        this.vpcId = newvalue;
    }

    /**
     * Gets the value of the state property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     *
     * @param newvalue
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setState(final String newvalue) {
        this.state = newvalue;
    }
}
