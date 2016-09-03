package com.tlswe.awsmock.common.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.tlswe.awsmock.common.exception.AwsMockException;

public class TemplateUtilsTest {

    private final String errFTemplateFile = "error.xml.ftl";

    @Test
    public void Test_get(){

        Map<String, Object> data = new HashMap<String, Object>();

        data.put("errorCode", "101");
        data.put("errorMessage", "Error happened!");
        data.put("requestID", "1");

        String output = TemplateUtils.get(errFTemplateFile, data);

        // check that the template file is populated
        Assert.assertTrue(output!=null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error happened!</Message>"));
        Assert.assertTrue(output.contains("<RequestID>1</RequestID>"));
    }

    @Test(expected=AwsMockException.class)
    public void Test_getNoFile(){

        Map<String, Object> data = new HashMap<String, Object>();

        TemplateUtils.get("notgiven", data);
    }

    @Test
    public void Test_write(){

        Map<String, Object> data = new HashMap<String, Object>();

        data.put("errorCode", "101");
        data.put("errorMessage", "Error happened!");
        data.put("requestID", "1");

        StringWriter writer = new StringWriter();
        TemplateUtils.write(errFTemplateFile, data, writer);
        String output = writer.toString();

        // check that the template file is populated
        Assert.assertTrue(output!=null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error happened!</Message>"));
        Assert.assertTrue(output.contains("<RequestID>1</RequestID>"));
    }

}
