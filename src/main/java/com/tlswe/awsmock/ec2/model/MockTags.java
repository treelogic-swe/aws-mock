package com.tlswe.awsmock.ec2.model;

import java.util.List;
import java.util.Map;

/**
 * The Class MockTags.
 */
public class MockTags {

    /** The resources set. */
    private List<String> resourcesSet;

    /** The tag set. */
    private Map<String, String> tagSet;

    /**
     * Gets the value of the resourcesSet property.
     *
     * @return
     *     possible object is
     *
     */
    public final List<String> getResourcesSet() {
        return resourcesSet;
    }

    /**
     * Sets the value of the resourcesSet property.
     *
     * @param value
     *     allowed object
     *
     */
    public final void setResourcesSet(final List<String> value) {
        this.resourcesSet = value;
    }

    /**
     * Gets the value of the tagSet property.
     *
     * @return
     *     possible object
     *
     */
    public final Map<String, String> getTagSet() {
        return tagSet;
    }

    /**
     * Sets the value of the tagSet property.
     *
     * @param value
     *     allowed object
     *
     */
    public final void setTagSet(final Map<String, String> value) {
        this.tagSet = value;
    }

}
