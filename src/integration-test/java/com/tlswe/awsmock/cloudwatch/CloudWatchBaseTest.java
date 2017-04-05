
/**
 * File name: BaseTest.java Author: Davinder Kumar Create date: Nov 9, 2016
 */
package com.tlswe.awsmock.cloudwatch;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;

/**
 * Base underlying class for doing the fundamental calls to aws Cloudwatch interfaces, with neat utility methods which can be
 * made use of by test cases that test aws-mock.
 *
 * @author Davinder Kumar
 */
public class CloudWatchBaseTest {

    /**
     * Name space request.
     */
    private static final String NAMESPACE = "AWS/EC2";

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(CloudWatchBaseTest.class);

    /**
     * One second in millisecond.
     */
    private static final int ONE_SECOND = 1000;

    /**
     * Hours to add start time.
     */
    private static final int HOURS = -5;

    /**
     * Property key for AWS access key.
     */
    private static final String PROPERTY_ACCESS_KEY = "aws.accessKey";

    /**
     * Property key for AWS secret key.
     */
    private static final String PROPERTY_SECRET_KEY = "aws.secretKey";

    /**
     * Property key for end point URL.
     */
    private static final String PROPERTY_ENDPOINT = "cloudwatch";

    /**
     * CloudWatch client singleton.
     */
    private static AmazonCloudWatchClient amazonCloudWatchClient;

    /**
     * Properties load from INTEGRATION_TEST_PROPERTIES_FILE}.
     */
    private static String INTEGRATION_TEST_PROPERTIES_FILE = "aws-mock.integration-test.properties";

    /**
     * Properties load from file {@link INTEGRATION_TEST_PROPERTIES_FILE}.
     */
    private static Properties testProperties;

    /**
     * Load test properties from file {@link INTEGRATION_TEST_PROPERTIES_FILE}.
     */
    private static synchronized void initTestProperties() {
        if (testProperties == null) {
            testProperties = new Properties();
            try {
                testProperties.load(Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(INTEGRATION_TEST_PROPERTIES_FILE));
            } catch (IOException e) {
                Assert.fail("fail to load '" + INTEGRATION_TEST_PROPERTIES_FILE + "' - "
                        + e.getMessage());
            }
        }
    }

    /**
     * Load Cloud Watch client URL from test properties and create an Cloud Watch client instance. Invoked after
     * {@link #initTestProperties()}.
     */
    private static synchronized void initCloudWatchClient() {
        if (amazonCloudWatchClient == null) {
            AWSCredentials credentials = new BasicAWSCredentials(
                    testProperties.getProperty(PROPERTY_ACCESS_KEY),
                    testProperties.getProperty(PROPERTY_SECRET_KEY));
            amazonCloudWatchClient = new AmazonCloudWatchClient(credentials);
            amazonCloudWatchClient.setEndpoint(testProperties
                    .getProperty(PROPERTY_ENDPOINT));
        }
    }

    /**
     * Read test properties, create Cloud watch client.
     */
    @BeforeClass
    public static void setup() {
        initTestProperties();
        initCloudWatchClient();
    }

    /**
     * GetMetricStaticticsTest to get the data points
     *
     * @return Datapoint
     */
    protected final Datapoint getMetricStaticticsTest(String metricName) {
        Datapoint dataPoint = null;
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
        request.setStartTime(new DateTime().plusHours(HOURS).toDate());
        request.withNamespace(NAMESPACE);
        request.withPeriod(60 * 60);
        request.withMetricName(metricName);
        request.withStatistics("Average", "SampleCount");
        request.withEndTime(new Date());
        GetMetricStatisticsResult result = amazonCloudWatchClient.getMetricStatistics(request);
        if (result != null && !result.getDatapoints().isEmpty()) {
            dataPoint = result.getDatapoints().get(0);
        }

        return dataPoint;
    }

    /**
     * describerAlarmsTest to get the data points
     *
     * @return MetricAlarm
     */
    protected final MetricAlarm describerAlarmsTest() {
    	MetricAlarm metricAlarm = null;
        DescribeAlarmsRequest describeAlarmsRequest = new DescribeAlarmsRequest();
        DescribeAlarmsResult result = amazonCloudWatchClient.describeAlarms(describeAlarmsRequest);
        
        if (result != null && !result.getMetricAlarms().isEmpty()) {
        	metricAlarm = result.getMetricAlarms().get(0);
        }

        return metricAlarm;
    }
}
