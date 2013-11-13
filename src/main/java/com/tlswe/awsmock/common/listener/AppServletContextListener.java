package com.tlswe.awsmock.common.listener;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PersistenceUtils;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.control.MockEc2Controller;
import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;

/**
 * A ServletContextListener that does initializing tasks on event that context started (e.g. load and restore persistent
 * runtime object) and finalizing on event that context destroyed (e.g. save runtime objects to persistence).
 *
 * @author xma
 *
 */
public class AppServletContextListener implements ServletContextListener {

    /**
     * Log writer for this class.
     */
    private final Logger log = org.slf4j.LoggerFactory.getLogger(AppServletContextListener.class);

    /**
     * Global switch for persistence.
     */
    private static boolean persistenceEnabled = Boolean
            .parseBoolean(PropertiesUtils.getProperty(Constants.PROP_NAME_PERSISTENCE_ENABLED));


    /**
     * Default constructor.
     */
    public AppServletContextListener() {

    }


    /**
     * We load the saved instances if persistence of enabled, on web application starting.
     *
     * @param sce
     *            the context event object
     */
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        log.info("aws-mock starting...");

        if (persistenceEnabled) {
            AbstractMockEc2Instance[] instanceArray = (AbstractMockEc2Instance[]) PersistenceUtils.loadAll();
            if (null != instanceArray) {
                MockEc2Controller.getInstance().restoreAllMockEc2Instances(Arrays.asList(instanceArray));
            }
        }
    }


    /**
     * We save the instances if persistence of enabled, on web application shutting-down.
     *
     * @param sce
     *            the context event object
     */
    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {

        if (persistenceEnabled) {
            Collection<AbstractMockEc2Instance> instances = MockEc2Controller.getInstance().getAllMockEc2Instances();

            for (AbstractMockEc2Instance instance : instances) {
                // cancel and destroy the internal timers for all instances on
                // web app stopping
                instance.destroyInternalTimer();
            }
            // put all instances into an array which is serializable and type-cast safe for persistence
            AbstractMockEc2Instance[] array = new AbstractMockEc2Instance[instances.size()];
            instances.toArray(array);
            PersistenceUtils.saveAll(array);
        }
        log.info("aws-mock stopped...");
    }
}
