package com.tlswe.awsmock.ec2.control;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.ec2.cxf_generated.DescribeVpcsResponseType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MockEC2QueryHandler.class})
public class MockEC2QueryHandlerTest {

    @Test
    public void Test_getInstance(){
        Assert.assertTrue(MockEC2QueryHandler.getInstance()!=null);
    }

    @Test
    public void Test_getXmlError() throws Exception{
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        String output = Whitebox.invokeMethod(handler, "getXmlError", "101","Error had taken place!");

        // check that the template file is populated
        Assert.assertTrue(output!=null && !output.isEmpty());
        Assert.assertTrue(output.contains("<Code>101</Code>"));
        Assert.assertTrue(output.contains("<Message>Error had taken place!</Message>"));

    }

    @Test
    public void Test_describeVpcs() throws Exception{
        MockEC2QueryHandler handler = MockEC2QueryHandler.getInstance();
        DescribeVpcsResponseType vpcsResponseType = Whitebox.invokeMethod(handler, "describeVpcs");

        Assert.assertTrue(vpcsResponseType!=null);
        Assert.assertTrue(vpcsResponseType.getVpcSet().getItem().size()==1);
        Assert.assertTrue(vpcsResponseType.getVpcSet().getItem().get(0).getVpcId().equals("vpc-6e6eb509"));
    }

}
