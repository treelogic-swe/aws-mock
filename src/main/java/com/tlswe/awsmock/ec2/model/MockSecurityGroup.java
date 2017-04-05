package com.tlswe.awsmock.ec2.model;

import java.util.List;
import java.util.Map;

/**
 * The Class MockSecurityGroup.
 */
public class MockSecurityGroup {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The owner id. */
    private String ownerId;

    /** The group id. */
    private String groupId;

    /** The group name. */
    private String groupName;

    /** The group description. */
    private String groupDescription;

    /** The vpc id. */
    private String vpcId;

    /** The ip permissions. */
    private List<MockIpPermissionType> ipPermissions;

    /** The ip permissions egress. */
    private List<MockIpPermissionType> ipPermissionsEgress;

    /** The tag set. */
    private Map<String, String> tagSet = null;

    /**
     * Gets the value of the ownerId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the value of the ownerId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public final void setOwnerId(final String value) {
        this.ownerId = value;
    }

    /**
     * Gets the value of the groupId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setGroupId(final String value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the groupName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setGroupName(final String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the groupDescription property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getGroupDescription() {
        return groupDescription;
    }

    /**
     * Sets the value of the groupDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setGroupDescription(final String value) {
        this.groupDescription = value;
    }

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
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setVpcId(final String value) {
        this.vpcId = value;
    }


    /**
     * Gets the ip permissions.
     *
     * @return the ip permissions
     */
    public final List<MockIpPermissionType> getIpPermissions() {
        return ipPermissions;
    }

    /**
     * Sets the ip permissions.
     *
     * @param value the new ip permissions
     */
    public final void setIpPermissions(final List<MockIpPermissionType> value) {
        this.ipPermissions = value;
    }

    /**
     * Gets the ip permissions egress.
     *
     * @return the ip permissions egress
     */
    public final List<MockIpPermissionType> getIpPermissionsEgress() {
        return ipPermissionsEgress;
    }

    /**
     * Sets the ip permissions egress.
     *
     * @param value the new ip permissions egress
     */
    public final void setIpPermissionsEgress(final List<MockIpPermissionType> value) {
        this.ipPermissionsEgress = value;
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
     * @param value the value
     */
    public final void setTagSet(final Map<String, String> value) {
        this.tagSet = value;
    }
}