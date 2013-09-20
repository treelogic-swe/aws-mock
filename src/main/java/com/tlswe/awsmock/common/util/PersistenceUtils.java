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
 * Simple utilities that can save/load runtime objects (e.g. all mock ec2 instances).
 *
 * @author xma
 *
 */
public final class PersistenceUtils {

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
    private static String persistenceStoreFile = PropertiesUtils
            .getProperty(Constants.PROP_NAME_PERSISTENCE_STORE_FILE);


    /**
     * Save object to the binary file defined as filename in property "persistence.store.file".
     *
     * @param obj
     *            the object to save, which should be serializable
     * @throws MockEc2Exception
     */
    public static void saveAll(final Object obj) {

        try {

            File file = new File(persistenceStoreFile);

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
            log.error("FileNotFoundException caught during saving object to file: {}", e.getMessage());
        } catch (IOException e) {
            log.error("IOException caught during saving object to file: {}", e.getMessage());
        }

    }


    /**
     * Load object from the binary file defined as filename in property.
     *
     * @return the loaded object.
     * @throws MockEc2Exception
     */
    public static Object loadAll() {

        Object ret = null;
        try {

            File file = new File(persistenceStoreFile);

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
            log.error("ClassNotFoundException caught during loading object from file: " + e.getMessage());
        }
        return ret;

    }

}
