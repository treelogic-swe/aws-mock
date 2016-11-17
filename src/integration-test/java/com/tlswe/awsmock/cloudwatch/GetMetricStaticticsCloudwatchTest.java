package com.tlswe.awsmock.cloudwatch;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.cloudwatch.model.Datapoint;

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
     * Test describing security group.
     */
    @Test(timeout = TIMEOUT_LEVEL1)
    public final void GetMetricStaticticsTest() {
        log.info("Start GetMetricStatictics Cloudwatch test");

        Datapoint dataPoint = getMetricStaticticsTest();

        Assert.assertNotNull("data point should not be null", dataPoint);
        Assert.assertNotNull("average should not be null", dataPoint.getAverage());
        Assert.assertNotNull("sample count should not be null", dataPoint.getSampleCount());
    }
}
