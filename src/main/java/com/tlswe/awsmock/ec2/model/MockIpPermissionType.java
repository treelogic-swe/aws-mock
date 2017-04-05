package com.tlswe.awsmock.ec2.model;

import java.util.List;

/**
 * The Class MockIpPermissionType.
 */
public class MockIpPermissionType {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The ip protocol. */
    private String ipProtocol;

    /** The from port. */
    private Integer fromPort;

    /** The to port. */
    private Integer toPort;

    /** The groups. */
    private List<MockUserIdGroupPair> groups;

    /** The ip ranges. */
    private List<String> ipRanges;

    /**
     * Gets the value of the ipProtocol property.
     *
     * @return
     *     possible object is
     *     {@link String }
     */
    public final String getIpProtocol() {
        return ipProtocol;
    }

    /**
     * Sets the value of the ipProtocol property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public final void setIpProtocol(final String value) {
        this.ipProtocol = value;
    }

    /**
     * Gets the value of the fromPort property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     */
    public final Integer getFromPort() {
        return fromPort;
    }

    /**
     * Sets the value of the fromPort property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }

     */
    public final void setFromPort(final Integer value) {
        this.fromPort = value;
    }

    /**
     * Gets the value of the toPort property.
     * @return
     *     possible object is
     *     {@link Integer }
     */
    public final Integer getToPort() {
        return toPort;
    }

    /**
     * Sets the value of the toPort property.
     * @param value
     *     allowed object is
     *     {@link Integer }
     */
    public final void setToPort(final Integer value) {
        this.toPort = value;
    }

    /**
     * Gets the groups.
     * @return the groups
     */
    public final List<MockUserIdGroupPair> getGroups() {
        return groups;
    }

    /**
     * Sets the groups.
     * @param value the new groups
     */
    public final void setGroups(final List<MockUserIdGroupPair> value) {
        this.groups = value;
    }

    /**
     * Gets the ip ranges.
     * @return the ip ranges
     */
    public final List<String> getIpRanges() {
        return ipRanges;
    }

    /**
     * Sets the ip ranges.
     *
     * @param value the new ip ranges
     */
    public final void setIpRanges(final List<String> value) {
        this.ipRanges = value;
    }

}