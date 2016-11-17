package com.tlswe.awsmock.cloudwatch.control;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.tlswe.awsmock.cloudwatch.cxf_generated.Datapoint;
import com.tlswe.awsmock.cloudwatch.cxf_generated.Datapoints;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResponse;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResult;
import com.tlswe.awsmock.cloudwatch.cxf_generated.ResponseMetadata;
import com.tlswe.awsmock.cloudwatch.cxf_generated.StandardUnit;
import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.common.util.TemplateUtils;

import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;
import com.tlswe.awsmock.cloudwatch.util.JAXBUtilCW;

/**
 * Class that handlers requests of AWS Query API for managing mock ec2
 * instances. This class works between All object of mock cloudwatch instances
 * are of the same type which is defined as property of
 * "cloudwatch.instance.class" in aws-mock.properties (or if not overridden, as
 * the default value defined in aws-mock-default.properties).
 *
 * @author Davinder
 *
 */
public final class MockCloudWatchQueryHandler {

    /**
     * Singleton instance of MockCloudWatchQueryHandler.
     */
    private static MockCloudWatchQueryHandler singletonMockCloudWatchQueryHandler = null;

    /**
     * Log writer for this class.
     */
    private final Logger log = LoggerFactory.getLogger(MockCloudWatchQueryHandler.class);

    /**
     * The xml template filename for error response body.
     */
    private static final String ERROR_RESPONSE_TEMPLATE = "error.xml.ftl";

    /**
     * Description for the link to AWS QUERY API reference.
     */
    private static final String REF_CLOUDWATCH_QUERY_API_DESC = "See http://docs.aws.amazon.com/AmazonCloudWatch/"
            + "latest/monitoring/WhatIsCloudWatch.html for building a valid query.";

    /**
     * Predefined AMIs, as properties of predefined.mock.ami.X in
     * aws-mock.properties (or if not overridden, as defined in
     * aws-mock-default.properties). We use {@link TreeSet} here so that those
     * AMIs are loaded and displayed (described) in the same order the are
     * defined in the .properties file.
     */
    private static final Set<String> MOCK_AMIS = new TreeSet<String>();

    /**
     * Predefined mock vpc id.
     */
    private static final String MOCK_VPC_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_VPC_ID);

    /**
     * Predefined mock vpc state.
     */
    private static final String MOCK_VPC_STATE = PropertiesUtils.getProperty(Constants.PROP_NAME_VPC_STATE);

    /**
     * Predefined mock private ip address.
     */
    private static final String MOCK_PRIVATE_IP_ADDRESS = PropertiesUtils
            .getProperty(Constants.PROP_NAME_PRIVATE_IP_ADDRESS);

    /**
     * Predefined mock subnet id.
     */
    private static final String MOCK_SUBNET_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_SUBNET_ID);

    /**
     * Predefined mock route table id.
     */
    private static final String MOCK_ROUTE_TABLE_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_ROUTE_TABLE_ID);

    /**
     * Predefined mock internet gateway id.
     */
    private static final String MOCK_GATEWAY_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_GATEWAY_ID);

    /**
     * Predefined mock security group id.
     */
    private static final String MOCK_SECURITY_GROUP_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_GROUP_ID);

    /**
     * Predefined mock security owner id.
     */
    private static final String MOCK_SECURITY_OWNER_ID = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_OWNER_ID);

    /**
     * Predefined mock security group name.
     */
    private static final String MOCK_SECURITY_GROUP_NAME = PropertiesUtils
            .getProperty(Constants.PROP_NAME_SECURITY_GROUP_NAME);

    /**
     * Predefined mock ip protocol.
     */
    private static final String MOCK_IP_PROTOCOL = PropertiesUtils.getProperty(Constants.PROP_NAME_IP_PROTOCOL);

    /**
     * Predefined mock cidr block.
     */
    private static final String MOCK_CIDR_BLOCK = PropertiesUtils.getProperty(Constants.PROP_NAME_CIDR_BLOCK);

    /**
     * Predefined mock source ip port.
     */
    private static final int MOCK_SOURCE_PORT = PropertiesUtils.getIntFromProperty(Constants.PROP_NAME_SOURCE_PORT);

    /**
     * Predefined mock destination ip port.
     */
    private static final int MOCK_DEST_PORT = PropertiesUtils.getIntFromProperty(Constants.PROP_NAME_DEST_PORT);

    /**
     * Predefined mock volume Id.
     */
    private static final String MOCK_VOLUME_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_VOLUME_ID);

    /**
     * Predefined mock instance Id.
     */
    private static final String MOCK_INSTANCE_ID = PropertiesUtils.getProperty(Constants.PROP_NAME_INSTANCE_ID);

    /**
     * Predefined mock volume Type.
     */
    private static final String MOCK_VOLUME_TYPE = PropertiesUtils.getProperty(Constants.PROP_NAME_VOLUME_TYPE);

    /**
     * Predefined mock volume Status.
     */
    private static final String MOCK_VOLUME_STATUS = PropertiesUtils.getProperty(Constants.PROP_NAME_VOLUME_STATUS);

    /**
     * The remaining paged records of instance IDs per token by
     * 'describeInstances'.
     */
    private static Map<String, Set<String>> token2RemainingDescribedInstanceIDs
                    = new ConcurrentHashMap<String, Set<String>>();

    /**
     * A common random generator.
     */
    private static Random random = new Random();

    /**
     * The chars used to generate tokens (those tokens in describeInstances
     * req/resp pagination).
     */
    private static final String TOKEN_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Token mid-string length.
     */
    private static final int AVAILABLE_IP_ADDRESS_COUNT = 251;

    /**
     * Token prefix length.
     *
     * AWS's token is 276 bytes in length (19 fixed + 240 generated per response
     * + 17 fixed), we just mock that way.
     */
    protected static final int TOKEN_PREFIX_LEN = 19;

    /**
     * Token suffix length.
     */
    protected static final int TOKEN_SUFFIX_LEN = 17;

    /**
     * Token mid-string length.
     */
    protected static final int TOKEN_MIDDLE_LEN = 240;

    /**
     * The prefix string, which would be determined on app startup.
     */
    protected static final String TOKEN_PREFIX;

    /**
     * The suffix string, which would be determined on app startup.
     */
    protected static final String TOKEN_SUFFIX;

    /**
     * Default page size for pagination in describeInstance response.
     */
    protected static final int MAX_RESULTS_DEFAULT = 1000;

    static {
        // DEFAULT_MOCK_PLACEMENT.setAvailabilityZone(PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_PLACEMENT));
        MOCK_AMIS.addAll(PropertiesUtils.getPropertiesByPrefix("predefined.mock.ami."));

        /*
         * We determine the token's prefix and suffix at the start of webapp and
         * don't change them.
         */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKEN_PREFIX_LEN; i++) {
            sb.append(TOKEN_DICT.charAt(random.nextInt(TOKEN_DICT.length())));
        }
        TOKEN_PREFIX = sb.toString();

        sb = new StringBuilder();
        for (int i = 0; i < TOKEN_SUFFIX_LEN; i++) {
            sb.append(TOKEN_DICT.charAt(random.nextInt(TOKEN_DICT.length())));
        }
        TOKEN_SUFFIX = sb.toString();
    }

    /**
     * Constructor of MockEC2QueryHandler is made private and only called once
     * by {@link #getInstance()}.
     */
    private MockCloudWatchQueryHandler() {
    }

    /**
     *
     * @return singleton instance of {@link MockCloudWatchQueryHandler}
     */
    public static MockCloudWatchQueryHandler getInstance() {
        if (null == singletonMockCloudWatchQueryHandler) {
            // "double lock lazy loading" for singleton instance loading on
            // first time usage
            synchronized (MockCloudWatchQueryHandler.class) {
                if (null == singletonMockCloudWatchQueryHandler) {
                    singletonMockCloudWatchQueryHandler = new MockCloudWatchQueryHandler();
                }
            }
        }
        return singletonMockCloudWatchQueryHandler;
    }

    /**
     * Hub method for parsing query prarmeters and generate and write xml
     * response.
     *
     * @param queryParams
     *            map of query parameters from http request, which is from
     *            standard AWS Query API
     * @param response
     *            http servlet response to handle with
     * @throws IOException
     *             in case of failure of getting response's writer
     *
     */
    public void handle(final Map<String, String[]> queryParams, final HttpServletResponse response)
           throws IOException {
        if (null == response) {
            // do nothing in case null is passed in
            return;
        }

        String responseXml = null;

        if (null == queryParams || queryParams.size() == 0) {
            // no params found at all - write an error xml response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseXml = getXmlError("InvalidQuery", "No parameter in query at all! " + REF_CLOUDWATCH_QUERY_API_DESC);
        } else {
            // parse the parameters in query
            String[] versionParamValues = queryParams.get("Version");

            if (null == versionParamValues || versionParamValues.length != 1) {
                // no version param found - write an error xml response
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseXml = getXmlError("InvalidQuery",
                   "There should be a parameter of 'Version' provided in the query! " + REF_CLOUDWATCH_QUERY_API_DESC);
            } else {

                String version = versionParamValues[0];

                String[] actions = queryParams.get("Action");

                if (null == actions || actions.length != 1) {
                    // no action found - write response for error
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseXml = getXmlError("InvalidQuery",
                    "There should be a parameter of 'Action' provided in the query! " + REF_CLOUDWATCH_QUERY_API_DESC);
                } else {

                    String action = actions[0];

                    try {

                        response.setStatus(HttpServletResponse.SC_OK);

                        if ("GetMetricStatistics".equals(action)) {

                            DateTimeZone zone = DateTimeZone.UTC;
                            DateTime startTime = new DateTime(queryParams.get("StartTime")[0], zone);
                            DateTime endTime = new DateTime(queryParams.get("EndTime")[0], zone);

                            String[] statistics = new String[2];
                            if (queryParams.containsKey("Statistics.member.1")) {
                                statistics[0] = queryParams.get("Statistics.member.1")[0];
                            }

                            if (queryParams.containsKey("Statistics.member.2")) {
                                statistics[1] = queryParams.get("Statistics.member.2")[0];
                            }

                            String metricName = queryParams.get("MetricName")[0];
                            int period = Integer.parseInt(queryParams.get("Period")[0]);
                            responseXml = JAXBUtilCW.marshall(
                                    getMetricStatistics(statistics, startTime, endTime, period, metricName),
                                    "GetMetricStatistics", version);
                        } else {
                            // unsupported/unimplemented action - write an
                            // error
                            // response
                            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                            String allImplementedActions = "runInstances|stopInstances|startInstances|"
                                    + "terminateInstances|describeInstances|describeImages";
                            responseXml = getXmlError("NotImplementedAction",
                                    "Action '" + action + "' has not been implemented yet in aws-mock. "
                                         + "For now we only support actions as following: " + allImplementedActions);
                        }
                    } catch (BadEc2RequestException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseXml = getXmlError("InvalidQuery",
                          "invalid request for '" + action + "'. " + e.getMessage() + REF_CLOUDWATCH_QUERY_API_DESC);
                    } catch (AwsMockException e) {
                        log.error("server error occured while processing '{}' request. {}", action, e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        responseXml = getXmlError("InternalError", e.getMessage());
                    } catch (Exception e) {
                        log.error("server error occured while processing '{}' request. {}", action, e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        responseXml = getXmlError("InternalError", e.getMessage());
                    }

                }
            }

        }

        // System.out.println(responseXml);
        response.getWriter().write(responseXml);
        response.getWriter().flush();

    }

    /**
     * Handles "GetMetricStatistics" request, as simple as without any filters
     * to use.
     *
     * @param statistics
     *            Metric statistics.
     * @param startTime
     *            Metric statistics start time.
     * @param endTime
     *            Metric statistics end time.
     * @param period
     *            Metric collection period.
     * @param metricName
     *            Metric Name.
     * @return a GetMetricStatisticsResult for metricName.
     */
    private GetMetricStatisticsResponse getMetricStatistics(final String[] statistics, final DateTime startTime,
            final DateTime endTime, final int period, final String metricName) {
        GetMetricStatisticsResponse ret = new GetMetricStatisticsResponse();
        GetMetricStatisticsResult result = new GetMetricStatisticsResult();
        Datapoints dataPoints = new Datapoints();

        int dataPointsCount = Seconds.secondsBetween(startTime, endTime).getSeconds() / period;
        DateTime newDate = startTime;
        for (int counterDp = 0; counterDp < dataPointsCount; counterDp++) {
            Datapoint dp = new Datapoint();
            DateTime timeStamp = newDate.plusSeconds(period);
            XMLGregorianCalendar timeStampXml = toXMLGregorianCalendar(timeStamp);
            dp.setTimestamp(timeStampXml);
            dp.setAverage(getMetricAverageValue(metricName));
            dp.setSampleCount(getMetricSampleCountValue(metricName));
            dp.setUnit(getMetricUnit(metricName));
            dataPoints.getMember().add(dp);
            newDate = timeStamp;
        }

        result.setDatapoints(dataPoints);
        result.setLabel(metricName);
        ret.setGetMetricStatisticsResult(result);
        ResponseMetadata responseMetadata = new ResponseMetadata();
        responseMetadata.setRequestId(UUID.randomUUID().toString());
        ret.setResponseMetadata(responseMetadata);
        return ret;
    }

    /**
     * Converts java.util.Date to javax.xml.datatype.XMLGregorianCalendar.
     *
     * @param date
     *            Date parameter.
     * @return a XMLGregorianCalendar for date.
     */
    private XMLGregorianCalendar toXMLGregorianCalendar(final DateTime date) {
        GregorianCalendar gCalendar = new GregorianCalendar(date.getZone().toTimeZone());
        gCalendar.setTime(date.toDate());
        XMLGregorianCalendar xmlGrogerianCalendar = null;
        try {
            xmlGrogerianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);

        } catch (DatatypeConfigurationException ex) {
            log.error(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
        return xmlGrogerianCalendar;
    }

    /**
     * Get the Metric Average value.
     *
     * @param metricName
     *            Metric Name parameter.
     * @return Metric average Value.
     */
    private double getMetricAverageValue(final String metricName) {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;

        if (metricName.equals(Constants.CPU_UTILIZATION)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_BYTES)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_OPS)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_BYTES)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_BYTES_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_BYTES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_OPS)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_OPS_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_OPS_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_IN)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_OUT)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_IN)) {
           min = Integer.
                   parseInt(PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_OUT)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_INSTANCE)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_SYSTEM)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_USAGE)) {
           min = Integer.parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_USAGE_RANGE_AVERAGE).split(",")[0]);
           max = Integer.parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_USAGE_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.ESTIMATED_CHARGES)) {
          min = Integer.parseInt(PropertiesUtils.getProperty(Constants.ESTIMATED_CHARGES_RANGE_AVERAGE).split(",")[0]);
          max = Integer.parseInt(PropertiesUtils.getProperty(Constants.ESTIMATED_CHARGES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_BALANCE)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_AVERAGE).split(",")[1]);
        }

        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * Get the Metric Sample Count value.
     *
     * @param metricName
     *            Metric Name parameter.
     * @return Metric Sample Count Value.
     */
    private double getMetricSampleCountValue(final String metricName) {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;

        if (metricName.equals(Constants.CPU_UTILIZATION)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_BYTES)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_OPS)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_BYTES)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_BYTES_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_BYTES_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_OPS)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_OPS_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_WRITE_OPS_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_IN)) {
            min = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_OUT)) {
            min = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_IN)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_OUT)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_INSTANCE)) {
            min = Integer.parseInt(PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_SAMPLECOUNT)
                    .split(",")[0]);
            max = Integer.parseInt(PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_SAMPLECOUNT)
                    .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_SYSTEM)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_USAGE)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_USAGE_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.CPU_CREDIT_USAGE_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.ESTIMATED_CHARGES)) {
          min = Integer.parseInt(PropertiesUtils.getProperty(
                  Constants.ESTIMATED_CHARGES_RANGE_SAMPLECOUNT).split(",")[0]);
          max = Integer.parseInt(PropertiesUtils.getProperty(
                  Constants.ESTIMATED_CHARGES_RANGE_SAMPLECOUNT).split(",")[1]);
         } else if (metricName.equals(Constants.CPU_CREDIT_BALANCE)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_SAMPLECOUNT).split(",")[1]);
        }

        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * Get the Metric Unit.
     *
     * @param metricName
     *            Metric Name parameter.
     * @return Standard Unit Value.
     */
    private StandardUnit getMetricUnit(final String metricName) {
        String result = null;

        if (metricName.equals(Constants.CPU_UTILIZATION)) {
            result = Constants.UNIT_PERCENT;
        } else if (metricName.equals(Constants.DISK_READ_BYTES)) {
            result = Constants.UNIT_BYTES;
        } else if (metricName.equals(Constants.DISK_READ_OPS)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.DISK_WRITE_BYTES)) {
            result = Constants.UNIT_BYTES;
        } else if (metricName.equals(Constants.DISK_WRITE_OPS)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.NETWORK_IN)) {
            result = Constants.UNIT_BYTES;
        } else if (metricName.equals(Constants.NETWORK_OUT)) {
            result = Constants.UNIT_BYTES;
        } else if (metricName.equals(Constants.NETWORK_PACKETS_IN)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.NETWORK_PACKETS_OUT)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_INSTANCE)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_SYSTEM)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.CPU_CREDIT_USAGE)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.ESTIMATED_CHARGES)) {
            result = Constants.UNIT_COUNT;
        } else if (metricName.equals(Constants.CPU_CREDIT_BALANCE)) {
            result = Constants.UNIT_COUNT;
        }

        return StandardUnit.fromValue(result);
    }

    /**
     * Generate error response body in xml and write it with writer.
     *
     * @param errorCode
     *            the error code wrapped in the xml response
     * @param errorMessage
     *            the error message wrapped in the xml response
     * @return xml body for an error message which can be recognized by AWS
     *         clients
     */
    private String getXmlError(final String errorCode, final String errorMessage) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errorCode", StringEscapeUtils.escapeXml(errorCode));
        data.put("errorMessage", StringEscapeUtils.escapeXml(errorMessage));
        // fake a random UUID as request ID
        data.put("requestID", UUID.randomUUID().toString());

        String ret = null;

        try {
            ret = TemplateUtils.get(ERROR_RESPONSE_TEMPLATE, data);
        } catch (AwsMockException e) {
            log.error("fatal exception caught: {}", e.getMessage());
            e.printStackTrace();
        }
        return ret;
    }

}
