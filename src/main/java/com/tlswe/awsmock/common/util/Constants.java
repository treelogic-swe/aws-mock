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
     * Property name for min boot time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_EC2_CLEANUP_TERMINATED_INSTANCES_TIME_SECONDS = "ec2.cleanupTerminatedInstances.time.seconds";

    /**
     * Property name for max shutdown time of a mock EC2 instance in milliseconds.
     */
    String PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME = "instance.max.shutdown.time";

    /**
     * Property name for max shutdown time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS = "instance.max.shutdown.time.seconds";

    /**
     * Property name for min shutdown time of a mock EC2 instance in milliseconds.
     */
    String PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME = "instance.min.shutdown.time";

    /**
     * Property name for min shutdown time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_INSTANCE_MIN_SHUTDOWN_TIME_SECONDS = "instance.min.shutdown.time.seconds";

    /**
     * Property name for max boot time of a mock EC2 instance in milliseconds.
     */
    String PROP_NAME_INSTANCE_MAX_BOOT_TIME = "instance.max.boot.time";

    /**
     * Property name for max boot time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_INSTANCE_MAX_BOOT_TIME_SECONDS = "instance.max.boot.time.seconds";

    /**
     * Property name for min boot time of a mock EC2 instance in milliseconds.
     */
    String PROP_NAME_INSTANCE_MIN_BOOT_TIME = "instance.min.boot.time";

    /**
     * Property name for min boot time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_INSTANCE_MIN_BOOT_TIME_SECONDS = "instance.min.boot.time.seconds";

    /**
     * Property name for vpc id.
     */
    String PROP_NAME_VPC_ID = "network.vpcId";

    /**
     * Property name for vpc state.
     */
    String PROP_NAME_VPC_STATE = "network.vpcState";

    /**
     * Property name for subnet id.
     */
    String PROP_NAME_SUBNET_ID = "network.subnetId";
    /**
     * Property name for private ip address.
     */
    String PROP_NAME_PRIVATE_IP_ADDRESS = "network.privateIpAddress";

    /**
     * Property name for route table id.
     */
    String PROP_NAME_ROUTE_TABLE_ID = "network.routeTableId";
    /**
     * Property name for internet gateway id.
     */
    String PROP_NAME_GATEWAY_ID = "network.internetGatewayId";

    /**
     * Property name for security group id.
     */
    String PROP_NAME_SECURITY_GROUP_ID = "network.securityGroupId";

    /**
     * Property name for security owner id.
     */
    String PROP_NAME_SECURITY_OWNER_ID = "network.securityOwnerId";

    /**
     * Property name for security owner id.
     */
    String PROP_NAME_SECURITY_GROUP_NAME = "network.securityGroupName";

    /**
     * Property name for ip protocol.
     */
    String PROP_NAME_IP_PROTOCOL = "network.ipProtocol";

    /**
     * Property name for cidr block.
     */
    String PROP_NAME_CIDR_BLOCK = "network.cidrBlock";

    /**
     * Property name for source ip port.
     */
    String PROP_NAME_SOURCE_PORT = "network.sourcePort";

    /**
     * Property name for destination ip port.
     */
    String PROP_NAME_DEST_PORT = "network.destPort";

    /**
     * Property name for volume Id.
     */
    String PROP_NAME_VOLUME_ID = "storage.volumeId";

    /**
     * Property name for instance Id.
     */
    String PROP_NAME_INSTANCE_ID = "storage.instanceId";

    /**
     * Property name for volume Type.
     */
    String PROP_NAME_VOLUME_TYPE = "storage.volumeType";

    /**
     * Property name for volume Status.
     */
    String PROP_NAME_VOLUME_STATUS = "storage.volumeStatus";

}
