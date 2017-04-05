package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
*  Class for mock Internet gateway for AWS.
* @author davinder Kumar
*/
public class MockInternetGateway implements Serializable {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Internet gateway Id.
     */
    private String internetGatewayId = null;

    /**
     *  List of attachment set.
     */
    private List<MockInternetGatewayAttachmentType> attachmentSet = null;

    /**
     * Map of tag Set.
     */
    private Map<String, String> tagSet = null;

    /**
     * Set the Internet Gateway Id.
     *
     * @return the internet gateway id
     */
    public final String getInternetGatewayId() {
        return internetGatewayId;
    }

    /**
     * Get the Internet Gateway Id.
     *
     * @param newinternetGatewayId the new internet gateway id
     */
    public final void setInternetGatewayId(final String newinternetGatewayId) {
        this.internetGatewayId = newinternetGatewayId;
    }

    /**
     * Get the attachment Set.
     *
     * @return the attachment set
     */
    public final List<MockInternetGatewayAttachmentType> getAttachmentSet() {
        return attachmentSet;
    }

    /**
     * Set the attachment Set.
     *
     * @param newattachmentSet the new attachment set
     */
    public final void setAttachmentSet(final List<MockInternetGatewayAttachmentType> newattachmentSet) {
        this.attachmentSet = newattachmentSet;
    }

    /**
     * Get the tag Set.
     *
     * @return the tag set
     */
    public final Map<String, String> getTagSet() {
        return tagSet;
    }

    /**
     * Set the tag Set.
     *
     * @param newtagSet the tag set
     */
    public final void setTagSet(final Map<String, String> newtagSet) {
        this.tagSet = newtagSet;
    }
}
