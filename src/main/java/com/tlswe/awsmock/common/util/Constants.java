package com.tlswe.awsmock.common.util;

/**
 * Some common constants, mainly the property names in aws-mock(-defalut).properties.
 *
 * @author xma
 *
 */
public interface Constants {

    /**
     * Property name for the switch that indicates whether persistence for runtime objects in aws-mock is enabled or
     * not.
     */
    String PROP_NAME_PERSISTENCE_ENABLED = "persistence.enabled";

    /**
     * Property name for the target file to which to make runtime objects persistent.
     */
    String PROP_NAME_PERSISTENCE_STORE_FILE = "persistence.store.file";

    /**
     * Property name for class name for objects of mock ec2 instances to be instantiated aws-mock.
     */
    String PROP_NAME_EC2_INSTANCE_CLASS = "ec2.instance.class";

    /**
     * Property name for mock EC2 placement.
     */
    String PROP_NAME_EC2_PLACEMENT = "ec2.placement";

    /**
     * Property name for EC2 api version to be working with elasticfox.
     */
    String PROP_NAME_EC2_API_VERSION_ELASTICFOX = "ec2.api.version.elasticfox";
    /**
     * Property name for the api version that matches the original wsdl from which we generate the web service stubs.
     */
    String PROP_NAME_EC2_API_VERSION_CURRENT_IMPL = "ec2.api.version.current.impl";

    /**
     * Property name for the switch that indicates whether this mock EC2 web service is compatible with elasticfox.
     */
    String PROP_NAME_ELASTICFOX_COMPATIBLE = "elasticfox.compatible";

    /**
     * Property name for the xml namespace that matches the original wsdl from which we generate the web service stubs.
     */
    String PROP_NAME_XMLNS_CURRENT = "xmlns.current";

    /**
     * Property name for max shutdown time of a mock EC2 instance.
     */
    String PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS = "instance.max.shutdown.time.seconds";

    /**
     * Property name for min shutdown time of a mock EC2 instance.
     */
    String PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME_SECONDS = "instance.min.shutdown.time.seconds";

    /**
     * Property name for max boot time of a mock EC2 instance.
     */
    String PROP_NAME_INSTANCE_MAX_BOOT_TIME_SECONDS = "instance.max.boot.time.seconds";

    /**
     * Property name for min boot time of a mock EC2 instance.
     */
    String PROP_NAME_INSTANCE_MIN_BOOT_TIME_SECONDS = "instance.min.boot.time.seconds";
}
