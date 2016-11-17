package com.tlswe.awsmock.common.util;

/**
 * Some common constants, mainly the property names in
 * aws-mock(-defalut).properties.
 *
 * @author xma
 *
 */
public interface Constants {

    /**
     * Property name for the switch that indicates whether persistence for
     * runtime objects in aws-mock is enabled or not.
     */
    String PROP_NAME_PERSISTENCE_ENABLED = "persistence.enabled";

    /**
     * Property name for the target file to which to make runtime objects
     * persistent.
     */
    String PROP_NAME_PERSISTENCE_STORE_FILE = "persistence.store.file";

    /**
     * Property name for class name for objects of mock ec2 instances to be
     * instantiated aws-mock.
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
     * Property name for the api version that matches the original wsdl from
     * which we generate the web service stubs.
     */
    String PROP_NAME_EC2_API_VERSION_CURRENT_IMPL = "ec2.api.version.current.impl";

    /**
     * Property name for the switch that indicates whether this mock EC2 web
     * service is compatible with elasticfox.
     */
    String PROP_NAME_ELASTICFOX_COMPATIBLE = "elasticfox.compatible";

    /**
     * Property name for the xml namespace that matches the original wsdl from
     * which we generate the web service stubs.
     */
    String PROP_NAME_XMLNS_CURRENT = "xmlns.current";

    /**
     * Property name for min boot time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_EC2_CLEANUP_TERMINATED_INSTANCES_TIME_SECONDS = "ec2.cleanupTerminatedInstances.time.seconds";

    /**
     * Property name for max shutdown time of a mock EC2 instance in
     * milliseconds.
     */
    String PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME = "instance.max.shutdown.time";

    /**
     * Property name for max shutdown time of a mock EC2 instance in seconds.
     */
    String PROP_NAME_INSTANCE_MAX_SHUTDOWN_TIME_SECONDS = "instance.max.shutdown.time.seconds";

    /**
     * Property name for min shutdown time of a mock EC2 instance in
     * milliseconds.
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

    /**
     * Metric name for CPUUtilization.
     */
    String CPU_UTILIZATION = "CPUUtilization";

    /**
     * Metric name for DiskReadBytes.
     */
    String DISK_READ_BYTES = "DiskReadBytes";

    /**
     * Metric name for DiskWriteBytes.
     */
    String DISK_WRITE_BYTES = "DiskWriteBytes";

    /**
     * Metric name for NetworkIn.
     */
    String NETWORK_IN = "NetworkIn";

    /**
     * Metric name for NetworkOut.
     */
    String NETWORK_OUT = "NetworkOut";

    /**
     * Metric name for CPUCreditUsage.
     */
    String CPU_CREDIT_USAGE = "CPUCreditUsage";

    /**
     * Metric name for CPUCreditBalance.
     */
    String CPU_CREDIT_BALANCE = "CPUCreditBalance";

    /**
     * Metric name for CPUCreditBalance.
     */
    String ESTIMATED_CHARGES = "EstimatedCharges";

    /**
     * Metric name for DiskReadBytes.
     */
    String DISK_READ_OPS = "DiskReadOps";

    /**
     * Metric name for DiskWriteOps.
     */
    String DISK_WRITE_OPS = "DiskWriteOps";

    /**
     * Metric name for NetworkPacketsIn.
     */
    String NETWORK_PACKETS_IN = "NetworkPacketsIn";

    /**
     * Metric name for NetworkPacketsOut.
     */
    String NETWORK_PACKETS_OUT = "NetworkPacketsOut";

    /**
     * Metric name for StatusCheckFailed.
     */
    String STATUS_CHECK_FAILED = "StatusCheckFailed";

    /**
     * Metric name for StatusCheckFailed_Instance.
     */
    String STATUS_CHECK_FAILED_INSTANCE = "StatusCheckFailed_Instance";

    /**
     * Metric name for StatusCheckFailed_System.
     */
    String STATUS_CHECK_FAILED_SYSTEM = "StatusCheckFailed_System";

    /**
     * Metric Unit for Count.
     */
    String UNIT_COUNT = "Count";

    /**
     * Metric Unit for Bytes.
     */
    String UNIT_BYTES = "Bytes";

    /**
     * Metric Unit for Percent.
     */
    String UNIT_PERCENT = "Percent";

    /**
     * Metric Unit for USD.
     */
    String UNIT_COST = "USD";

    /**
     * Metric average statistics.
     */
    String AVG_STATISTICS = "Average";

    /**
     * Metric count statistics.
     */
    String SAMPLECOUNT_STATISTICS = "SampleCount";

    /**
     * CPU_UTILIZATION range average.
     */
    String CPU_UTILIZATION_RANGE_AVERAGE = "CPUUtilization.range.average";

    /**
     * CPU_UTILIZATION range sample count.
     */
    String CPU_UTILIZATION_RANGE_SAMPLECOUNT = "CPUUtilization.range.samplecount";

    /**
     * DISK_READ_BYTES range average.
     */
    String DISK_READ_BYTES_RANGE_AVERAGE = "DiskReadBytes.range.average";

    /**
     * DISK_READ_BYTES range sample count.
     */
    String DISK_READ_BYTES_RANGE_SAMPLECOUNT = "DiskReadBytes.range.samplecount";

    /**
     * DISK_WRITE_BYTES range average.
     */
    String DISK_WRITE_BYTES_RANGE_AVERAGE = "DiskWriteBytes.range.average";

    /**
     * DISK_WRITE_BYTES range sample count.
     */
    String DISK_WRITE_BYTES_RANGE_SAMPLECOUNT = "DiskWriteBytes.range.samplecount";

    /**
     * NETWORK_IN range average.
     */
    String NETWORK_IN_RANGE_AVERAGE = "NetworkIn.range.average";

    /**
     * NETWORK_IN range sample count.
     */
    String NETWORK_IN_RANGE_SAMPLECOUNT = "NetworkIn.range.samplecount";

    /**
     * NETWORK_OUT range average.
     */
    String NETWORK_OUT_RANGE_AVERAGE = "NetworkOut.range.average";

    /**
     * NETWORK_OUT range sample count.
     */
    String NETWORK_OUT_RANGE_SAMPLECOUNT = "NetworkOut.range.samplecount";

    /**
     * CPU_CREDIT_USAGE range average.
     */
    String CPU_CREDIT_USAGE_RANGE_AVERAGE = "CPUCreditUsage.range.average";

    /**
     * CPU_CREDIT_USAGE range sample count.
     */
    String CPU_CREDIT_USAGE_RANGE_SAMPLECOUNT = "CPUCreditUsage.range.samplecount";

    /**
     * CPU_CREDIT_USAGE range average.
     */
    String ESTIMATED_CHARGES_RANGE_AVERAGE = "EstimatedCharges.range.average";

    /**
     * CPU_CREDIT_USAGE range sample count.
     */
    String ESTIMATED_CHARGES_RANGE_SAMPLECOUNT = "EstimatedCharges.range.samplecount";

    /**
     * CPU_CREDIT_BALANCE range average.
     */
    String CPU_CREDIT_BALANCE_RANGE_AVERAGE = "CPUCreditBalance.range.average";

    /**
     * CPU_CREDIT_BALANCE range sample count.
     */
    String CPU_CREDIT_BALANCE_RANGE_SAMPLECOUNT = "CPUCreditBalance.range.samplecount";

    /**
     * DISK_READ_OPS range average.
     */
    String DISK_READ_OPS_RANGE_AVERAGE = "DiskReadOps.range.average";

    /**
     * DISK_READ_OPS range sample count.
     */
    String DISK_READ_OPS_RANGE_SAMPLECOUNT = "DiskReadOps.range.samplecount";

    /**
     * DISK_WRITE_OPS range average.
     */
    String DISK_WRITE_OPS_RANGE_AVERAGE = "DiskWriteOps.range.average";

    /**
     * DISK_WRITE_OPS range sample count.
     */
    String DISK_WRITE_OPS_RANGE_SAMPLECOUNT = "DiskWriteOps.range.samplecount";

    /**
     * NETWORK_PACKETS_IN range average.
     */
    String NETWORK_PACKETS_IN_RANGE_AVERAGE = "NetworkPacketsIn.range.average";

    /**
     * NETWORK_PACKETS_IN range sample count.
     */
    String NETWORK_PACKETS_IN_RANGE_SAMPLECOUNT = "NetworkPacketsIn.range.samplecount";

    /**
     * NETWORK_PACKETS_OUT range average.
     */
    String NETWORK_PACKETS_OUT_RANGE_AVERAGE = "NetworkPacketsOut.range.average";

    /**
     * NETWORK_PACKETS_OUT range sample count.
     */
    String NETWORK_PACKETS_OUT_RANGE_SAMPLECOUNT = "NetworkPacketsOut.range.samplecount";

    /**
     * STATUS_CHECK_FAILED range average.
     */
    String STATUS_CHECK_FAILED_RANGE_AVERAGE = "StatusCheckFailed.range.average";

    /**
     * STATUS_CHECK_FAILED range sample count.
     */
    String STATUS_CHECK_FAILED_RANGE_SAMPLECOUNT = "StatusCheckFailed.range.samplecount";

    /**
     * STATUS_CHECK_FAILED_INSTANCE range average.
     */
    String STATUS_CHECK_FAILED_INSTANCE_RANGE_AVERAGE = "StatusCheckFailed_Instance.range.average";

    /**
     * STATUS_CHECK_FAILED_INSTANCE range sample count.
     */
    String STATUS_CHECK_FAILED_INSTANCE_RANGE_SAMPLECOUNT = "StatusCheckFailed_Instance.range.samplecount";

    /**
     * STATUS_CHECK_FAILED_SYSTEM range average.
     */
    String STATUS_CHECK_FAILED_SYSTEM_RANGE_AVERAGE = "StatusCheckFailed_System.range.average";

    /**
     * STATUS_CHECK_FAILED_SYSTEM range sample count.
     */
    String STATUS_CHECK_FAILED_SYSTEM_RANGE_SAMPLECOUNT = "StatusCheckFailed_System.range.samplecount";

    /**
     * Property name for the api version that matches the original wsdl from
     * which we generate the web service stubs.
     */
    String PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL = "cloudwatch.api.version.current.impl";

    /**
     * Property name for the xml namespace that matches the original wsdl from
     * which we generate the web service stubs.
     */
    String PROP_NAME_CLOUDWATCH_XMLNS_CURRENT = "cloudwatch.xmlns.current";

}
