package com.tlswe.awsmock.ec2.model;

/**
 * The Class MockUserIdGroupPair.
 */
public class MockUserIdGroupPair {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The user id. */
    private String userId;

    /** The group id. */
    private String groupId;

    /** The group name. */
    private String groupName;

    /**
     * Gets the value of the userId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setUserId(final String value) {
        this.userId = value;
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
}