package com.tlswe.awsmock.cloudwatch.control;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.cloudwatch.cxf_generated.Datapoint;
import com.tlswe.awsmock.cloudwatch.cxf_generated.Datapoints;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResponse;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResult;
import com.tlswe.awsmock.cloudwatch.cxf_generated.ResponseMetadata;
import com.tlswe.awsmock.cloudwatch.cxf_generated.StandardUnit;
import com.tlswe.awsmock.cloudwatch.util.JAXBUtilCW;
import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.common.util.TemplateUtils;
import com.tlswe.awsmock.ec2.exception.BadEc2RequestException;

/**
 * Class that handlers requests of AWS CloudWatch Query API for monitoring the mock ec2
 * instances. This class works between All object of mock CloudWatch.
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
     * A common random generator.
     */
    private static Random random = new Random();

    /**
     * The chars used to generate tokens (those tokens in describeInstances
     * req/resp pagination).
     */
    private static final String TOKEN_DICT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

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
            responseXml = getXmlError("InvalidQuery",
                    "No parameter in query at all! " + REF_CLOUDWATCH_QUERY_API_DESC);
        } else {
            // parse the parameters in query
            String[] versionParamValues = queryParams.get("Version");

            if (null == versionParamValues || versionParamValues.length != 1) {
                // no version param found - write an error xml response
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseXml = getXmlError("InvalidQuery",
                        "There should be a parameter of 'Version' provided in the query! "
                                + REF_CLOUDWATCH_QUERY_API_DESC);
            } else {

                String version = versionParamValues[0];

                String[] actions = queryParams.get("Action");

                if (null == actions || actions.length != 1) {
                    // no action found - write response for error
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    responseXml = getXmlError("InvalidQuery",
                            "There should be a parameter of 'Action' provided in the query! "
                                    + REF_CLOUDWATCH_QUERY_API_DESC);
                } else {

                    String action = actions[0];

                    try {

                        response.setStatus(HttpServletResponse.SC_OK);

                        if ("GetMetricStatistics".equals(action)) {

                            DateTimeZone zone = DateTimeZone.UTC;
                            DateTime startTime = new DateTime(queryParams.get("StartTime")[0],
                                    zone);
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
                                    getMetricStatistics(statistics, startTime, endTime, period,
                                            metricName),
                                    "GetMetricStatistics", version);
                        } else {
                            // unsupported/unimplemented action - write an
                            // error
                            // response
                            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                            String allImplementedActions = "runInstances|stopInstances|startInstances|"
                                    + "terminateInstances|describeInstances|describeImages";
                            responseXml = getXmlError("NotImplementedAction",
                                    "Action '" + action
                                            + "' has not been implemented yet in aws-mock. "
                                            + "For now we only support actions as following: "
                                            + allImplementedActions);
                        }
                    } catch (BadEc2RequestException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        responseXml = getXmlError("InvalidQuery",
                                "invalid request for '" + action + "'. " + e.getMessage()
                                        + REF_CLOUDWATCH_QUERY_API_DESC);
                    } catch (AwsMockException e) {
                        log.error("server error occured while processing '{}' request. {}", action,
                                e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        responseXml = getXmlError("InternalError", e.getMessage());
                    } catch (Exception e) {
                        log.error("server error occured while processing '{}' request. {}", action,
                                e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        if (e.getMessage() != null) {
                             responseXml = getXmlError("InternalError", e.getMessage());
                        } else {
                             responseXml = "InternalError";
                        }
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
    private GetMetricStatisticsResponse getMetricStatistics(final String[] statistics,
            final DateTime startTime,
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
        int min = 0;
        int max = 1;

        if (metricName.equals(Constants.CPU_UTILIZATION)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.CPU_UTILIZATION_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.CPU_UTILIZATION_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_BYTES)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_READ_BYTES_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_READ_BYTES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_OPS)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_READ_OPS_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_READ_OPS_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_BYTES)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_WRITE_BYTES_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_WRITE_BYTES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_OPS)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_WRITE_OPS_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.DISK_WRITE_OPS_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_IN)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_IN_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_OUT)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_OUT_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_IN)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.NETWORK_PACKETS_IN_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.NETWORK_PACKETS_IN_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_OUT)) {
            min = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_AVERAGE)
                                    .split(",")[0]);
            max = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_AVERAGE)
                                    .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED)) {
            min = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_AVERAGE)
                                    .split(",")[0]);
            max = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_AVERAGE)
                                    .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_INSTANCE)) {
            min = Integer.parseInt(
                    PropertiesUtils
                            .getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_AVERAGE)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils
                            .getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_AVERAGE)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_SYSTEM)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_AVERAGE)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_AVERAGE)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_USAGE)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.CPU_CREDIT_USAGE_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.CPU_CREDIT_USAGE_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.ESTIMATED_CHARGES)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.ESTIMATED_CHARGES_RANGE_AVERAGE).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.ESTIMATED_CHARGES_RANGE_AVERAGE).split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_BALANCE)) {
            min = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_AVERAGE).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_AVERAGE).split(",")[1]);
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
        int min = 0;
        int max = 1;

        if (metricName.equals(Constants.CPU_UTILIZATION)) {
            min = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_SAMPLECOUNT)
                                    .split(",")[0]);
            max = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.CPU_UTILIZATION_RANGE_SAMPLECOUNT)
                                    .split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_BYTES)) {
            min = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_SAMPLECOUNT)
                                    .split(",")[0]);
            max = Integer
                    .parseInt(
                            PropertiesUtils.getProperty(Constants.DISK_READ_BYTES_RANGE_SAMPLECOUNT)
                                    .split(",")[1]);
        } else if (metricName.equals(Constants.DISK_READ_OPS)) {
            min = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils.getProperty(Constants.DISK_READ_OPS_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_BYTES)) {
            min = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.DISK_WRITE_BYTES_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.DISK_WRITE_BYTES_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.DISK_WRITE_OPS)) {
            min = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.DISK_WRITE_OPS_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.DISK_WRITE_OPS_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_IN)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.NETWORK_IN_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.NETWORK_IN_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_OUT)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.NETWORK_OUT_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.NETWORK_OUT_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_IN)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_IN_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.NETWORK_PACKETS_OUT)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.NETWORK_PACKETS_OUT_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.STATUS_CHECK_FAILED_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_INSTANCE)) {
            min = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_SAMPLECOUNT)
                    .split(",")[0]);
            max = Integer.parseInt(PropertiesUtils
                    .getProperty(Constants.STATUS_CHECK_FAILED_INSTANCE_RANGE_SAMPLECOUNT)
                    .split(",")[1]);
        } else if (metricName.equals(Constants.STATUS_CHECK_FAILED_SYSTEM)) {
            min = Integer.parseInt(
                    PropertiesUtils
                            .getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils
                            .getProperty(Constants.STATUS_CHECK_FAILED_SYSTEM_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_USAGE)) {
            min = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.CPU_CREDIT_USAGE_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer
                    .parseInt(PropertiesUtils
                            .getProperty(Constants.CPU_CREDIT_USAGE_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
        } else if (metricName.equals(Constants.ESTIMATED_CHARGES)) {
            min = Integer.parseInt(PropertiesUtils.getProperty(
                    Constants.ESTIMATED_CHARGES_RANGE_SAMPLECOUNT).split(",")[0]);
            max = Integer.parseInt(PropertiesUtils.getProperty(
                    Constants.ESTIMATED_CHARGES_RANGE_SAMPLECOUNT).split(",")[1]);
        } else if (metricName.equals(Constants.CPU_CREDIT_BALANCE)) {
            min = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_SAMPLECOUNT)
                            .split(",")[0]);
            max = Integer.parseInt(
                    PropertiesUtils.getProperty(Constants.CPU_CREDIT_BALANCE_RANGE_SAMPLECOUNT)
                            .split(",")[1]);
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
