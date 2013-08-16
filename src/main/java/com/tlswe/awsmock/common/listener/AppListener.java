package com.tlswe.awsmock.common.listener;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.tlswe.awsmock.common.util.PersistenceUtils;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.control.MockEc2Controller;
import com.tlswe.awsmock.ec2.model.MockEc2Instance;

/**
 * Listener that does initializing tasks on context started (e.g. load and
 * restore persistent runtime object) and finalizing on context destroyed (e.g.
 * save runtime objects to persistence).
 * 
 * @author xma
 * 
 */
public class AppListener implements ServletContextListener {

    /**
     * Log writer for this class.
     */
    Logger _log = org.slf4j.LoggerFactory.getLogger(AppListener.class);

    /**
     * Global switch for persistence.
     */
    private static boolean _persistenceEnabled = Boolean.parseBoolean(PropertiesUtils
            .getProperty("persistence.enabled"));

    /**
     * Default constructor.
     */
    public AppListener() {

    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent sce) {
        _log.info("aws-mock starting...");
        if (_persistenceEnabled) {
            ArrayList<MockEc2Instance> instances = (ArrayList<MockEc2Instance>) PersistenceUtils.loadAll();
            MockEc2Controller.restoreAllMockEc2Instances(instances);
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {

        if (_persistenceEnabled) {
            Collection<MockEc2Instance> instances = MockEc2Controller.getAllMockEc2Instances();

            for (MockEc2Instance instance : instances) {
                // cancel and destroy the internal timers for all instances on
                // web app stopping
                instance.destroyInternalTimer();
            }
            // put all instances into an ArrayList which is serializable
            ArrayList<MockEc2Instance> list = new ArrayList<MockEc2Instance>();
            list.addAll(instances);
            PersistenceUtils.saveAll(list);
        }
        _log.info("aws-mock stopped...");
    }

}
