package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.Map;

/**
 * The Class MockSubnet.
 */
public class MockSubnet implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The subnet id. */
    private String subnetId = null;

    /** The state. */
    private String state = null;

    /** The vpc id. */
    private String vpcId = null;

    /** The cidr block. */
    private String cidrBlock = null;

    /** The available ip address count. */
    private Integer availableIpAddressCount = null;

    /** The availability zone. */
    private String availabilityZone = null;

    /** The default for az. */
    private Boolean defaultForAz = false;

    /** The map public ip on launch. */
    private Boolean mapPublicIpOnLaunch = false;

    /** The tag set. */
    private Map<String, String> tagSet = null;

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
     * Gets the vpc id.
     *
     * @return the vpc id
     */
    public final String getVpcId() {
        return vpcId;
    }

    /**
     * Sets the vpc id.
     *
     * @param newvpcId the new vpc id
     */
    public final void setVpcId(final String newvpcId) {
        this.vpcId = newvpcId;
    }

    /**
     * Gets the cidr block.
     *
     * @return the cidr block
     */
    public final String getCidrBlock() {
        return cidrBlock;
    }

    /**
     * Sets the cidr block.
     *
     * @param newcidrBlock the new cidr block
     */
    public final void setCidrBlock(final String newcidrBlock) {
        this.cidrBlock = newcidrBlock;
    }

    /**
     * Gets the available ip address count.
     *
     * @return the available ip address count
     */
    public final Integer getAvailableIpAddressCount() {
        return availableIpAddressCount;
    }

    /**
     * Sets the available ip address count.
     *
     * @param newavailableIpAddressCount the new available ip address count
     */
    public final void setAvailableIpAddressCount(final Integer newavailableIpAddressCount) {
        this.availableIpAddressCount = newavailableIpAddressCount;
    }

    /**
     * Gets the availability zone.
     *
     * @return the availability zone
     */
    public final String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
     * Sets the availability zone.
     *
     * @param newavailabilityZone the new availability zone
     */
    public final void setAvailabilityZone(final String newavailabilityZone) {
        this.availabilityZone = newavailabilityZone;
    }

    /**
     * Gets the default for az.
     *
     * @return the default for az
     */
    public final Boolean getDefaultForAz() {
        return defaultForAz;
    }

    /**
     * Sets the default for az.
     *
     * @param newdefaultForAz the new default for az
     */
    public final void setDefaultForAz(final Boolean newdefaultForAz) {
        this.defaultForAz = newdefaultForAz;
    }

    /**
     * Gets the map public ip on launch.
     *
     * @return the map public ip on launch
     */
    public final Boolean getMapPublicIpOnLaunch() {
        return mapPublicIpOnLaunch;
    }

    /**
     * Sets the map public ip on launch.
     *
     * @param newmapPublicIpOnLaunch the new map public ip on launch
     */
    public final void setMapPublicIpOnLaunch(final Boolean newmapPublicIpOnLaunch) {
        this.mapPublicIpOnLaunch = newmapPublicIpOnLaunch;
    }

    /**
     * Gets the tag set.
     *
     * @return the tag set
     */
    public final Map<String, String> getTagSet() {
        return tagSet;
    }

    /**
     * Sets the tag set.
     *
     * @param newtagSet the tag set
     */
    public final void setTagSet(final Map<String, String> newtagSet) {
        this.tagSet = newtagSet;
    }
}
