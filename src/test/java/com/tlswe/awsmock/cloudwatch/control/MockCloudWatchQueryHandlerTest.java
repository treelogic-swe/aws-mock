package com.tlswe.awsmock.cloudwatch.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.cloudwatch.cxf_generated.StandardUnit;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResponse;
import com.tlswe.awsmock.cloudwatch.util.JAXBUtilCW;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockCloudWatchQueryHandler.class, JAXBUtilCW.class })
public class MockCloudWatchQueryHandlerTest {

    private static Properties properties = new Properties();
    private static final String INVALID_QUERY = "<Code>InvalidQuery</Code>";
    private static final String NO_PARAM_IN_QUERY = "No parameter in query at all!";
    private static final String NO_VERSION_IN_QUERY = "There should be a parameter of &apos;Version&apos; provided in the query!";
    private static final String NO_ACTION_IN_QUERY = "There should be a parameter of &apos;Action&apos; provided in the query!";
    private static final String DUMMY_XML_RESPONSE = "Dummy XML Response";
    private static final String ACTION_KEY = "Action";
    private static final String VERSION_KEY = "Version";
    private static final String VERSION_1 = "version1";

    static {
        InputStream inputStream = null;

        // first load default properties from aws-mock-default.properties
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(PropertiesUtils.FILE_NAME_AWS_MOCK_DEFAULT_PROPERTIES);
        if (null == inputStream) {
            // do nothing
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // then load user-defined overriding properties from aws-mock.properties
        // if it exists in classpath
        inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(PropertiesUtils.FILE_NAME_AWS_MOCK_PROPERTIES);
        if (null == inputStream) {
            // do nothing
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void doSetup() {
        PowerMockito.mockStatic(JAXBUtilCW.class);
    }

    @Test
    public void Test_getInstance() {
        Assert.assertTrue(MockCloudWatchQueryHandler.getInstance() != null);
    }

    @Test
    public void Test_getXmlError() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        String output = Whitebox.invokeMethod(handler, "getXmlError", "101",
                "Error had taken place!");

        // check that the template file is populated
        Assert.assertTrue(output != null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error had taken place!</Message>"));

    }

    @Test
    public void Test_getMetricStatistics() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "CPUUtilization");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForDiskReadBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "DiskReadBytes");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForStatusCheckFailed() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "StatusCheckFailed");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForStatusCheckFailed_Instance() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "StatusCheckFailed_Instance");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForStatusCheckFailed_System() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "StatusCheckFailed_System");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForNetworkIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "NetworkIn");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForNetworkOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "NetworkOut");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForDiskWriteOps() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, "DiskWriteOps");
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForCPU_CREDIT_USAGE() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.CPU_CREDIT_USAGE);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForDiskReadOps() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.DISK_READ_OPS);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForDiskWriteBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.DISK_WRITE_BYTES);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForNetworkPacketsIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.NETWORK_PACKETS_IN);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForNetworkPacketsOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.NETWORK_PACKETS_OUT);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_getMetricStatisticsForESTIMATED_CHARGES() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-1);
        String[] statistics = { "Average", "SampleCount" };
        GetMetricStatisticsResponse getMetric = Whitebox.invokeMethod(handler,
                "getMetricStatistics", statistics,
                startTime, new DateTime(), 60 * 60, Constants.ESTIMATED_CHARGES);
        Assert.assertTrue(getMetric != null);
        Assert.assertTrue(
                getMetric.getGetMetricStatisticsResult().getDatapoints().getMember().size() == 1);
    }

    @Test
    public void Test_toXMLGregorianCalendar() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime();
        XMLGregorianCalendar xmlGrogerianCalendar = Whitebox.invokeMethod(handler,
                "toXMLGregorianCalendar", startTime);
        Assert.assertTrue(xmlGrogerianCalendar != null);
        Assert.assertTrue(xmlGrogerianCalendar.isValid() == true);
    }

    @Test
    public void Test_toXMLGregorianCalendarForLastTwoHours() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        DateTime startTime = new DateTime().plusHours(-3);
        XMLGregorianCalendar xmlGrogerianCalendar = Whitebox.invokeMethod(handler,
                "toXMLGregorianCalendar", startTime);
        Assert.assertTrue(xmlGrogerianCalendar != null);
        Assert.assertTrue(xmlGrogerianCalendar.isValid() == true);
    }

    @Test
    public void Test_getMetricAverageValueForDiskWriteOps() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "DiskWriteOps");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValueForNetworkOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue", "NetworkOut");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValueForDiskReadBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "DiskReadBytes");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValueForDiskWriteBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "DiskWriteBytes");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValueForNetworkIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "NetworkIn");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValueForNetworkPacketsOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "NetworkPacketsOut");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricAverageValue() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double averageValue = Whitebox.invokeMethod(handler, "getMetricAverageValue",
                "CPUUtilization");
        Assert.assertTrue(averageValue > 0);
    }

    @Test
    public void Test_getMetricSampleCountValueForDiskReadBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "DiskReadBytes");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueForNetworkOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "NetworkOut");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueForNetworkIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "NetworkIn");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValue() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "CPUUtilization");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueDiskWriteOps() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "DiskWriteOps");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueDiskReadOps() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "DiskReadOps");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueDiskReadBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "DiskReadBytes");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricSampleCountValueNetworkPacketsIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        double sampleCount = Whitebox.invokeMethod(handler, "getMetricSampleCountValue",
                "NetworkPacketsIn");
        Assert.assertTrue(sampleCount > -1);
    }

    @Test
    public void Test_getMetricUnitForNetworkIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit", "NetworkIn");
        Assert.assertTrue(unit.compareTo(StandardUnit.BYTES) == 0);
    }

    @Test
    public void Test_getMetricUnitForNetworkOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit", "NetworkOut");
        Assert.assertTrue(unit.compareTo(StandardUnit.BYTES) == 0);
    }

    @Test
    public void Test_getMetricUnitForDiskReadBytes() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit", "DiskReadBytes");
        Assert.assertTrue(unit.compareTo(StandardUnit.BYTES) == 0);
    }

    @Test
    public void Test_getMetricUnit() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit", "CPUUtilization");
        Assert.assertTrue(unit.compareTo(StandardUnit.PERCENT) == 0);
    }

    @Test
    public void Test_getMetricUnitForNetworkPacketsIn() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit",
                Constants.NETWORK_PACKETS_IN);
        Assert.assertTrue(unit.compareTo(StandardUnit.COUNT) == 0);
    }

    @Test
    public void Test_getMetricUnitForNetworkPacketsOut() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit",
                Constants.NETWORK_PACKETS_OUT);
        Assert.assertTrue(unit.compareTo(StandardUnit.COUNT) == 0);
    }

    @Test
    public void Test_getMetricUnitForCpuCreditUsage() throws Exception {
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();
        StandardUnit unit = Whitebox.invokeMethod(handler, "getMetricUnit",
                Constants.CPU_CREDIT_USAGE);
        Assert.assertTrue(unit.compareTo(StandardUnit.COUNT) == 0);
    }

    @Test
    public void Test_handleNoParams() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(null, null); // does nothing

        handler.handle(null, response); // no query params

        String responseString = sw.toString();
        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_PARAM_IN_QUERY));

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        handler.handle(new HashMap<String, String[]>(), response); // no query
                                                                   // params

        responseString = sw.toString();
        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_PARAM_IN_QUERY));
    }

    @Test
    public void Test_handleImproperVersionParams() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        // no version key here
        queryParams.put("someKey", new String[] { "someValue" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_VERSION_IN_QUERY));

        // more than two version values here
        queryParams.put(VERSION_KEY, new String[] { VERSION_1, "version2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, response);

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_VERSION_IN_QUERY));

    }

    @Test
    public void Test_handleImproperActionParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        // no action key provided
        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_ACTION_IN_QUERY));

        // more than two action values here
        queryParams.put(ACTION_KEY, new String[] { "action1", "action2" });

        sw = new StringWriter();
        pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        handler.handle(queryParams, response);

        Assert.assertTrue(responseString.contains(INVALID_QUERY));
        Assert.assertTrue(responseString.contains(NO_ACTION_IN_QUERY));
    }

    @Test
    public void Test_handleUnsupportedActionParam() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "unsupportedAction" });

        handler.handle(queryParams, response);

        String responseString = sw.toString();

        Assert.assertTrue(responseString.contains("NotImplementedAction"));

    }

    @Test
    public void Test_handleGetMetricStatistics() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtilCW.marshall(Mockito.any(GetMetricStatisticsResponse.class),
                Mockito.eq("GetMetricStatistics"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "GetMetricStatistics" });
        queryParams.put("StartTime", new String[] {
                new DateTime().plusHours(-2).toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("EndTime",
                new String[] { new DateTime().toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("MetricName", new String[] { "CPUUtilization" });
        queryParams.put("Period", new String[] { "3600" });
        queryParams.put("Statistics.member.1", new String[] { "Average" });
        queryParams.put("Statistics.member.2", new String[] { "SampleCount" });
        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleGetMetricStatisticsForNetworkIn() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtilCW.marshall(Mockito.any(GetMetricStatisticsResponse.class),
                Mockito.eq("GetMetricStatistics"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "GetMetricStatistics" });
        queryParams.put("StartTime", new String[] {
                new DateTime().plusHours(-2).toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("EndTime",
                new String[] { new DateTime().toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("MetricName", new String[] { "NetworkIn" });
        queryParams.put("Period", new String[] { "3600" });
        queryParams.put("Statistics.member.1", new String[] { "Average" });
        queryParams.put("Statistics.member.2", new String[] { "SampleCount" });
        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleGetMetricStatisticsForNetworkOut() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtilCW.marshall(Mockito.any(GetMetricStatisticsResponse.class),
                Mockito.eq("GetMetricStatistics"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "GetMetricStatistics" });
        queryParams.put("StartTime", new String[] {
                new DateTime().plusHours(-2).toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("EndTime",
                new String[] { new DateTime().toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("MetricName", new String[] { "NetworkOut" });
        queryParams.put("Period", new String[] { "3600" });
        queryParams.put("Statistics.member.1", new String[] { "Average" });
        queryParams.put("Statistics.member.2", new String[] { "SampleCount" });
        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleGetMetricStatisticsForDiskReadBytes() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtilCW.marshall(Mockito.any(GetMetricStatisticsResponse.class),
                Mockito.eq("GetMetricStatistics"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "GetMetricStatistics" });
        queryParams.put("StartTime", new String[] {
                new DateTime().plusHours(-2).toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("EndTime",
                new String[] { new DateTime().toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("MetricName", new String[] { "DiskReadBytes" });
        queryParams.put("Period", new String[] { "3600" });
        queryParams.put("Statistics.member.1", new String[] { "Average" });
        queryParams.put("Statistics.member.2", new String[] { "SampleCount" });
        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }

    @Test
    public void Test_handleGetMetricStatisticsForDiskReadOps() throws IOException {

        HttpServletResponse response = Mockito.spy(HttpServletResponse.class);
        MockCloudWatchQueryHandler handler = MockCloudWatchQueryHandler.getInstance();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Mockito.when(response.getWriter()).thenReturn(pw);
        Mockito.when(JAXBUtilCW.marshall(Mockito.any(GetMetricStatisticsResponse.class),
                Mockito.eq("GetMetricStatistics"), Mockito.eq(VERSION_1)))
                .thenReturn(DUMMY_XML_RESPONSE);

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        queryParams.put(VERSION_KEY, new String[] { VERSION_1 });
        queryParams.put(ACTION_KEY, new String[] { "GetMetricStatistics" });
        queryParams.put("StartTime", new String[] {
                new DateTime().plusHours(-2).toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("EndTime",
                new String[] { new DateTime().toString(ISODateTimeFormat.dateTime()) });
        queryParams.put("MetricName", new String[] { "DiskReadOps" });
        queryParams.put("Period", new String[] { "3600" });
        queryParams.put("Statistics.member.1", new String[] { "Average" });
        queryParams.put("Statistics.member.2", new String[] { "SampleCount" });
        handler.handle(queryParams, response);

        String responseString = sw.toString();
        Assert.assertTrue(responseString.equals(DUMMY_XML_RESPONSE));
    }
}
