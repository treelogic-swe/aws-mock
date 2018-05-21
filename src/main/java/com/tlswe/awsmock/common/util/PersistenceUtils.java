package com.tlswe.awsmock.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utilities that can save/load runtime objects (e.g. all mock ec2/vpc/subnet/volume instances).
 *
 * @author xma
 *
 */
public final class PersistenceUtils {

    /**
     * The Enum PersistenceStoreType.
     */
    public enum PersistenceStoreType {

        /** The ec2. */
        EC2("ec2.save"),

        /** The vpc. */
        VPC("vpc.save"),

        /** The subnet. */
        SUBNET("subnet.save"),

        /** The routetable. */
        ROUTETABLE("routetable.save"),

        /** The volume. */
        VOLUME("volume.save"),

        /** The internetgateway. */
        INTERNETGATEWAY("internetgateway.save"),

        /** The tags. */
        TAGS("tag.save");

        /**
         * Name of PersistenceStoreType.
         */
        private String store;

        /**
         * Private constructor for the enums of PersistenceStoreType defined above.
         *
         * @param storeName the store name
         */
        PersistenceStoreType(final String storeName) {
            this.store = storeName;
        }

        /**
         * Get the PersistenceStoreType .
         *
         * @return Persistence Store Type of the enum.
         */
        public String getStore() {
            return this.store;
        }

        /**
         * Tests if an PersistenceStore Type of the given name exists as among all.
         *
         * @param storeType
         *            Persistence Store Type
         * @return true for existing and false for not existing
         */
        public static boolean containsByName(final String storeType) {
            PersistenceStoreType[] values = PersistenceStoreType.values();
            for (PersistenceStoreType value : values) {
                if (value.name().equals(storeType)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Get enum of an Persistence Store Type of the given store exists as
         * among all the defined PersistenceStoreTypes.
         *
         * @param storeType
         *            Persistence Store Type
         * @return object of PersistenceStoreType, null will be returned in case that not found
         */
        public static PersistenceStoreType getByName(final String storeType) {
            PersistenceStoreType[] values = PersistenceStoreType.values();
            for (PersistenceStoreType value : values) {
                if (value.name().equals(storeType)) {
                    return value;
                }
            }
            return null;
        }

    }

    /**
     * Constructor is made private as this is a utility class which should be always used in static way.
     */
    private PersistenceUtils() {

    }

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(PersistenceUtils.class);

    /**
     * The persistent file path for saving object.
     */
    private static String persistenceStorePath = PropertiesUtils
            .getProperty(Constants.PROP_NAME_PERSISTENCE_STORE_PATH);

    /**
     * Save object to the binary file defined as filename in property "persistence.store.file".
     *
     * @param obj            the object to save, which should be serializable
     * @param storeType the store type
     */
    public static void saveAll(final Object obj, final PersistenceStoreType storeType) {

        try {

            File file = new File(persistenceStorePath + File.separator + storeType);

            // create necessary parent directories on file system
            File directory = file.getParentFile();
            if (null != directory && !directory.exists()) {
                directory.mkdirs();
            }

            log.info("aws-mock: saving to {}", file.getAbsolutePath());

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file, false));
            out.writeObject(obj);
            out.close();
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException caught during saving object to file: {}",
                    e.getMessage());
        } catch (IOException e) {
            log.error("IOException caught during saving object to file: {}", e.getMessage());
        }

    }

    /**
     * Load object from the binary file defined as filename in property.
     *
     * @param storeType the store type
     * @return the loaded object.
     */
    public static Object loadAll(final PersistenceStoreType storeType) {

        Object ret = null;
        try {

            File file = new File(persistenceStorePath + File.separator + storeType);

            log.info("aws-mock: try to load objects from {}", file.getAbsolutePath());

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            ret = in.readObject();
            in.close();

        } catch (FileNotFoundException e) {
            // no saved file to load from
            log.warn("no saved file to load from: {}", e.getMessage());
            return null;
        } catch (IOException e) {
            // failed to load from the saved file
            log.warn("failed to load from the saved file: {}", e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException caught during loading object from file: "
                    + e.getMessage());
        }
        return ret;

    }

}
