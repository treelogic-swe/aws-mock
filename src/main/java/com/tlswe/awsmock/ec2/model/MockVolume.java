package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;

/**
 * The Class MockVolume.
 */
public class MockVolume implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The size. */
    private String size = null;

    /** The snapshot id. */
    private String snapshotId = null;

    /** The availability zone. */
    private String availabilityZone = null;

    /** The volume type. */
    private String volumeType = null;

    /** The iops. */
    private Integer iops = null;

    /** The volume id. */
    private String volumeId = null;

    /**
     * Gets the volume id.
     *
     * @return the volume id
     */
    public final String getVolumeId() {
        return volumeId;
    }

    /**
     * Sets the volume id.
     *
     * @param newvolumeId the new volume id
     */
    public final void setVolumeId(final String newvolumeId) {
        this.volumeId = newvolumeId;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public final String getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param newsize the new size
     */
    public final void setSize(final String newsize) {
        this.size = newsize;
    }

    /**
     * Gets the snapshot id.
     *
     * @return the snapshot id
     */
    public final String getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the snapshot id.
     *
     * @param newsnapshotId the new snapshot id
     */
    public final void setSnapshotId(final String newsnapshotId) {
        this.snapshotId = newsnapshotId;
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
     * Gets the volume type.
     *
     * @return the volume type
     */
    public final String getVolumeType() {
        return volumeType;
    }

    /**
     * Sets the volume type.
     *
     * @param newvolumeType the new volume type
     */
    public final void setVolumeType(final String newvolumeType) {
        this.volumeType = newvolumeType;
    }

    /**
     * Gets the iops.
     *
     * @return the iops
     */
    public final Integer getIops() {
        return iops;
    }

    /**
     * Sets the iops.
     *
     * @param newiops the new iops
     */
    public final void setIops(final Integer newiops) {
        this.iops = newiops;
    }
}
