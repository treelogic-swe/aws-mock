package com.tlswe.awsmock.cloudwatch.util;

import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.cloudwatch.control.MockCloudWatchQueryHandler;
import com.tlswe.awsmock.cloudwatch.cxf_generated.DescribeAlarmsResponse;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResponse;
import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;
import com.tlswe.awsmock.ec2.util.JAXBUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockCloudWatchQueryHandler.class, PropertiesUtils.class })
public class JAXBUtilCWTest {

    @Test
    public void Test_marshall() throws Exception {

        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "CPUUtilization");
        String xml = JAXBUtilCW.marshall(getMetric, "GetMetricStatisticsResponse", "2010-08-01");

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<Label>CPUUtilization</Label>"));
    }

    @Test
    public void Test_mashallNotElasticFox() throws Exception {

        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                .thenReturn("false");
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "DiskReadBytes");
        String xml = JAXBUtilCW.marshall(getMetric, "GetMetricStatisticsResponse", null);

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<Label>DiskReadBytes</Label>"));
    }

    @Test
    public void Test_mashallNetworkIn() throws Exception {

        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                .thenReturn("true");
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX))
                .thenReturn("2010-11-15");
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "NetworkIn");
        String xml = JAXBUtilCW.marshall(getMetric, "GetMetricStatisticsResponse", null);

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(xml.contains("<Label>NetworkIn</Label>"));
    }

    @Test
    public void Test_mashallReplaceVersionWithElasticFoxVersion() throws Exception {
        PowerMockito.spy(PropertiesUtils.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "NetworkOut");

        String xml = JAXBUtilCW.marshall(getMetric, "getMetricStatistics", PropertiesUtils
                .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL));

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(
                xml.contains("xmlns:ns2=\"http://monitoring.amazonaws.com/doc/" + PropertiesUtils
                        .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL)));
        Assert.assertTrue(xml.contains("<Label>NetworkOut</Label>"));
    }

    @Test
    public void Test_mashallDescribeAlarams() throws Exception {
        PowerMockito.spy(PropertiesUtils.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DescribeAlarmsResponse alarms = Whitebox.invokeMethod(handler,
                "describeAlarms");

        String xml = JAXBUtilCW.marshall(alarms, "describeAlarms", PropertiesUtils
                .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL));

        Assert.assertTrue(xml != null && !xml.isEmpty());
        Assert.assertTrue(
                xml.contains("xmlns:ns2=\"http://monitoring.amazonaws.com/doc/" + PropertiesUtils
                        .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL)));
    }
    
    @Test
    public void Test_mashallDescribeAlaramsElasticFoxTrue() throws Exception {

        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                .thenReturn("true");
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX))
                .thenReturn("2010-08-01");

        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DescribeAlarmsResponse alarms = Whitebox.invokeMethod(handler,
                "describeAlarms");

        String xml = JAXBUtilCW.marshall(alarms, "describeAlarms", PropertiesUtils
                .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL));

        Assert.assertTrue(xml != null && !xml.isEmpty());
    }

    @Test
    public void Test_mashallDescribeAlaramsElasticFoxFalse() throws Exception {

        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                .thenReturn("false");
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX))
                .thenReturn("2010-08-01");

        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DescribeAlarmsResponse alarms = Whitebox.invokeMethod(handler,
                "describeAlarms");

        String xml = JAXBUtilCW.marshall(alarms, "describeAlarms", PropertiesUtils
                .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL));

        Assert.assertTrue(xml != null && !xml.isEmpty());
    }

    @Test(expected = AwsMockException.class)
    public void Test_marshallGetMetricStatisticsFailed() throws Exception {
        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_CLOUDWATCH_XMLNS_CURRENT))
                .thenThrow(JAXBException.class);
        JAXBUtilCW.marshall(new GetMetricStatisticsResponse(), "Test", "2012-02-10");
    }

    @Test(expected = AwsMockException.class)
    public void Test_marshallDescribeAlarmsFailed() throws Exception {
        PowerMockito.spy(PropertiesUtils.class);
        Mockito.when(PropertiesUtils.getProperty(Constants.PROP_NAME_CLOUDWATCH_XMLNS_CURRENT))
                .thenThrow(JAXBException.class);
        JAXBUtilCW.marshall(new DescribeAlarmsResponse(), "Test", "2012-02-10");
    }
}
