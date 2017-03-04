package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * The Class MockVpc.
 */
public class MockVpc implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The vpc id. */
    private String vpcId = null;

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
     * Gets the dhcp options id.
     *
     * @return the dhcp options id
     */
    public final String getDhcpOptionsId() {
        return dhcpOptionsId;
    }

    /**
     * Sets the dhcp options id.
     *
     * @param newdhcpOptionsId the new dhcp options id
     */
    public final void setDhcpOptionsId(final String newdhcpOptionsId) {
        this.dhcpOptionsId = newdhcpOptionsId;
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

    /**
     * Gets the instance tenancy.
     *
     * @return the instance tenancy
     */
    public final String getInstanceTenancy() {
        return instanceTenancy;
    }

    /**
     * Sets the instance tenancy.
     *
     * @param newinstanceTenancy the new instance tenancy
     */
    public final void setInstanceTenancy(final String newinstanceTenancy) {
        this.instanceTenancy = newinstanceTenancy;
    }

    /**
     * Gets the checks if is default.
     *
     * @return the checks if is default
     */
    public final Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * Sets the checks if is default.
     *
     * @param newisDefault the new checks if is default
     */
    public final void setIsDefault(final Boolean newisDefault) {
        this.isDefault = newisDefault;
    }

    /** The state. */
    private String state = null;

    /** The cidr block. */
    private String cidrBlock = null;

    /** The dhcp options id. */
    private String dhcpOptionsId = null;

    /** The tag set. */
    private Map<String, String> tagSet = null;

    /** The instance tenancy. */
    private String instanceTenancy = null;

    /** The is default. */
    private Boolean isDefault = false;
}
