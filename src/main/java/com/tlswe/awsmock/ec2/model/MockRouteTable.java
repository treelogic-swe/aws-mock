package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The Class MockRouteTable.
 */
public class MockRouteTable implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The route table id. */
    private String routeTableId = null;

    /** The vpc id. */
    private String vpcId = null;

    /** The route set. */
    private List<MockRoute> routeSet = null;

    /** The association set. */
    private List<MockRouteTableAssoication> associationSet = null;

    /** The propagating vgw set. */
    private List<String> propagatingVgwSet = null;

    /** The tag set. */
    private Map<String, String> tagSet = null;

    /**
    * Gets the value of the routeSet property.
    *
    * @return
    *     possible object is
    *     List {@link MockRoute}
    *
    */
    public final List<MockRoute> getRouteSet() {
        return routeSet;
    }

    /**
     * Sets the value of the routeSet property.
     *
     * @param newrouteSet
     *     allowed object is
     *     List of {@link MockRoute }
     *
     */
    public final void setRouteSet(final List<MockRoute> newrouteSet) {
        this.routeSet = newrouteSet;
    }

    /**
     * Gets the value of the associationSet property.
     *
     * @return
     *     possible object is
     *     List of {@link MockRouteTableAssoication }
     *
     */
    public final List<MockRouteTableAssoication> getAssociationSet() {
        return associationSet;
    }

    /**
     * Sets the value of the associationSet property.
     *
     * @param newassociationSet
     *     allowed object is
     *     List of  {@link MockRouteTableAssoication }
     *
     */
    public final void setAssociationSet(final List<MockRouteTableAssoication> newassociationSet) {
        this.associationSet = newassociationSet;
    }

    /**
     * Gets the value of the propagatingVgwSet property.
     *
     * @return
     *     possible object is
     *     List {@link String }
     *
     */
    public final List<String> getPropagatingVgwSet() {
        return propagatingVgwSet;
    }

    /**
     * Sets the value of the propagatingVgwSet property.
     *
     * @param newpropagatingVgwSet
     *     allowed object is
     *     List of {@link String }
     *
     */
    public final void setPropagatingVgwSet(final List<String> newpropagatingVgwSet) {
        this.propagatingVgwSet = newpropagatingVgwSet;
    }

    /**
     * Gets the value of the tagSet property.
     *
     * @return
     *     possible object is
     *     Map.
     *
     */
    public final Map<String, String> getTagSet() {
        return tagSet;
    }

    /**
     * Sets the value of the tagSet property.
     *
     * @param newtagSet
     *     allowed object is
     *     Map
     *
     */
    public final void setTagSet(final Map<String, String> newtagSet) {
        this.tagSet = newtagSet;
    }

    /**
     * Gets the value of the routeTableId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public final String getRouteTableId() {
        return routeTableId;
    }

    /**
     * Sets the value of the routeTableId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public final void setRouteTableId(final String value) {
        this.routeTableId = value;
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
}
