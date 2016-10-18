package com.tlswe.awsmock.common.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.exception.AwsMockException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, Template.class})
public class TemplateUtilsTest {

    private final String errFTemplateFile = "error.xml.ftl";

    @Before
    public void doSetup(){
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(TemplateUtils.class,"/templates");
        Whitebox.setInternalState(TemplateUtils.class, "conf", config);
    }

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

    @Test(expected=AwsMockException.class)
    public void Test_getIOException() throws Exception{

        Configuration config = Mockito.mock(Configuration.class);

        Whitebox.setInternalState(TemplateUtils.class, "conf", config);
        Mockito.doNothing().when(config)
                .setClassForTemplateLoading(Mockito.eq(TemplateUtils.class), Mockito.anyString());
        Mockito.when(config.getTemplate(Mockito.anyString())).thenThrow(new IOException("Force IOException"));

        Map<String, Object> data = new HashMap<String, Object>();
        TemplateUtils.get(errFTemplateFile, data);


    }

    @Test(expected=AwsMockException.class)
    public void Test_getProcessTemplateExceptionWithData() throws Exception{

        Configuration config = Mockito.mock(Configuration.class);
        Template template = Mockito.mock(Template.class);

        Whitebox.setInternalState(TemplateUtils.class, "conf", config);
        Mockito.doNothing().when(config)
                .setClassForTemplateLoading(Mockito.eq(TemplateUtils.class), Mockito.anyString());

        Mockito.when(config.getTemplate(Mockito.anyString())).thenReturn(template);
        Mockito.doThrow(new TemplateException("Forced TemplateException", null)).when(template).process(Mockito.any(), Mockito.any(Writer.class));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errorCode", "101");
        data.put("errorMessage", "Error happened!");
        data.put("requestID", "1");

        TemplateUtils.get(errFTemplateFile, data);

    }

    @Test(expected=AwsMockException.class)
    public void Test_getProcessTemplateExceptionWithNoData() throws Exception{

        Configuration config = Mockito.mock(Configuration.class);
        Template template = Mockito.mock(Template.class);

        Whitebox.setInternalState(TemplateUtils.class, "conf", config);
        Mockito.doNothing().when(config)
                .setClassForTemplateLoading(Mockito.eq(TemplateUtils.class), Mockito.anyString());

        Mockito.when(config.getTemplate(Mockito.anyString())).thenReturn(template);
        Mockito.doThrow(new TemplateException("Forced TemplateException", null)).when(template).process(Mockito.any(), Mockito.any(Writer.class));

        TemplateUtils.get(errFTemplateFile, null);

    }

    @Test(expected=AwsMockException.class)
    public void Test_getProcessIOExceptionWithNoData() throws Exception{

        Configuration config = Mockito.mock(Configuration.class);
        Template template = Mockito.mock(Template.class);

        Whitebox.setInternalState(TemplateUtils.class, "conf", config);
        Mockito.doNothing().when(config)
                .setClassForTemplateLoading(Mockito.eq(TemplateUtils.class), Mockito.anyString());

        Mockito.when(config.getTemplate(Mockito.anyString())).thenReturn(template);
        Mockito.doThrow(new IOException("Forced IOException", null)).when(template).process(Mockito.any(), Mockito.any(Writer.class));

        Map<String, Object> data = new HashMap<String, Object>();
        TemplateUtils.get(errFTemplateFile, data);

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
