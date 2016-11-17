package com.tlswe.awsmock.cloudwatch.servlet;

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

import com.tlswe.awsmock.cloudwatch.control.MockCloudWatchQueryHandler;
import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MockCloudWatchQueryHandler.class, HttpServletRequest.class, HttpServletResponse.class})
public class MockCloudWatchEndpointServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    MockCloudWatchQueryHandler handler;

    Map<String, String[]> queryParams = new HashMap<String, String[]>();

    @Before
    public void doSetup(){
      PowerMockito.mockStatic(MockCloudWatchQueryHandler.class);
      Mockito.when(MockCloudWatchQueryHandler.getInstance()).thenReturn(handler);
      Mockito.when(request.getParameterMap()).thenReturn(queryParams);
    }

    @Test
    public void Test_doPost() throws IOException{
        MockCloudWatchEndpointServlet mockCloudWatchEndpointServlet = new MockCloudWatchEndpointServlet();
        mockCloudWatchEndpointServlet.doPost(request, response);
    }

    @Test
    public void Test_doGet() throws IOException{
        MockCloudWatchEndpointServlet mockCloudWatchEndpointServlet = new MockCloudWatchEndpointServlet();
        mockCloudWatchEndpointServlet.doPost(request, response);
    }

}
