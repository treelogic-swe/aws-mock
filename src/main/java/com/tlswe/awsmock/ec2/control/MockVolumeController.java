package com.tlswe.awsmock.ec2.control;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.tlswe.awsmock.ec2.model.MockVolume;

/**
 * Factory class providing static methods for managing life cycle of mock Volume. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock Volume only. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockVolumeController {

    /**
     * Singleton instance of MockVolumeController.
     */
    private static MockVolumeController singletonMockVolumeController = null;

    /**
     * Length of generated postfix of volume ID.
     */
    protected static final short VOLUME_ID_POSTFIX_LENGTH = 17;

    /**
     * A map of all the mock Volumes, instanceID as key and {@link MockVolume} as value.
     */
    private final Map<String, MockVolume> allMockVolumes = new ConcurrentHashMap<String, MockVolume>();

    /**
     * Constructor of MockVolumeController is made private and only called once by {@link #getInstance()}.
     */
    private MockVolumeController() {

    }

    /**
     *
     * @return singleton instance of {@link MockVolumeController}
     */
    public static MockVolumeController getInstance() {
        if (null == singletonMockVolumeController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockVolumeController.class) {
                if (null == singletonMockVolumeController) {
                    singletonMockVolumeController = new MockVolumeController();
                }
            }
        }
        return singletonMockVolumeController;
    }

    /**
     * List mock Volume instances in current aws-mock.
     *
     * @return a collection of {@link MockVolumeController} with specified instance IDs, or all of the mock Volume.
     */
    public Collection<MockVolume> describeVolumes() {
        return allMockVolumes.values();
    }

    /**
    * Create the mock Volume.
    * @param volumeType of Volume.
    * @param size : Volume size.
    * @param availabilityZone : Volume availability zone.
    * @param iops : Volume iops count
    * @param snapshotId : Volume's SnapshotId.
    * @return mock Volume.
    */
    public MockVolume createVolume(
            final String volumeType, final String size, final String availabilityZone,
            final int iops,
            final String snapshotId) {

        MockVolume ret = new MockVolume();
        ret.setVolumeType(volumeType);
        ret.setVolumeId(
                "vol-" + UUID.randomUUID().toString().substring(0, VOLUME_ID_POSTFIX_LENGTH));
        ret.setSize(size);
        ret.setAvailabilityZone(availabilityZone);
        ret.setIops(iops);
        ret.setSnapshotId(snapshotId);

        allMockVolumes.put(ret.getVolumeId(), ret);
        return ret;
    }

    /**
     * Delete Mock Volume.
     *
     * @param volumeId
     *            volumeId to be deleted
     * @return Mock volume.
     */
    public MockVolume deleteVolume(final String volumeId) {

        if (volumeId != null && allMockVolumes.containsKey(volumeId)) {
            return  allMockVolumes.remove(volumeId);
        }

        return null;
    }

    /**
     * Clear {@link #allMockVolumes} and restore it from given a collection of instances.
     *
     * @param volumes
     *            collection of Volumes to restore
     */
    public void restoreAllMockVolume(final Collection<MockVolume> volumes) {
        allMockVolumes.clear();
        if (null != volumes) {
            for (MockVolume instance : volumes) {
                allMockVolumes.put(instance.getVolumeId(), instance);
            }
        }
    }
}
