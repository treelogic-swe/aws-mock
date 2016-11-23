package com.tlswe.awsmock.cloudwatch;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.tlswe.awsmock.common.util.Constants;

public class GetMetricStaticticsCloudwatchTest extends CloudWatchBaseTest {

     /**
      * 2 minutes timeout.
      */
     private static final int TIMEOUT_LEVEL1 = 120000;
    
     /**
     * Log writer for this class.
     */
     private static Logger log = LoggerFactory.getLogger(GetMetricStaticticsCloudwatchTest.class);

	/**
     * Test GetMetricStatictics for CPUUtilization.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTest() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.CPU_UTILIZATION);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }

    /**
     * Test GetMetricStatictics for Disk Read bytes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForDiskReadBytes() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.DISK_READ_BYTES);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }

    /**
     * Test GetMetricStatictics for Disk Write bytes.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForDiskWriteBytes() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.DISK_WRITE_BYTES);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }

    /**
     * Test GetMetricStatictics for Disk Read Ops.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForDiskReadOps() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.DISK_READ_OPS);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }

    /**
     * Test GetMetricStatictics for Disk Write Ops.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForDiskWriteOps() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.DISK_WRITE_OPS);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }
   
    /**
     * Test GetMetricStatictics for Network In.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForNetworkIn() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.NETWORK_IN);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }
 
    /**
     * Test GetMetricStatictics for Network Out.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTestForNetworkOut() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest(Constants.NETWORK_OUT);

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }
}
