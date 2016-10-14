package com.tlswe.awsmock.ec2.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MockEC2QueryHandler.class, HttpServletRequest.class, HttpServletResponse.class})
public class MockEc2EndpointServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    MockEC2QueryHandler handler;

    Map<String, String[]> queryParams = new HashMap<String, String[]>();

    @Before
    public void doSetup(){
      PowerMockito.mockStatic(MockEC2QueryHandler.class);
      Mockito.when(MockEC2QueryHandler.getInstance()).thenReturn(handler);
      Mockito.when(request.getParameterMap()).thenReturn(queryParams);
    }

    @Test
    public void Test_doPost() throws IOException{
        MockEc2EndpointServlet mockEc2EndpointServlet = new MockEc2EndpointServlet();
        mockEc2EndpointServlet.doPost(request, response);
    }

    @Test
    public void Test_doGet() throws IOException{
        MockEc2EndpointServlet mockEc2EndpointServlet = new MockEc2EndpointServlet();
        mockEc2EndpointServlet.doPost(request, response);
    }

}
