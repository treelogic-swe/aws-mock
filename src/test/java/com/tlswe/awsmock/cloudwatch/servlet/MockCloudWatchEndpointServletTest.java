package com.tlswe.awsmock.cloudwatch.servlet;

import com.tlswe.awsmock.cloudwatch.control.MockCloudWatchQueryHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockCloudWatchQueryHandler.class, HttpServletRequest.class,
        HttpServletResponse.class })
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*" })
public class MockCloudWatchEndpointServletTest {

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    MockCloudWatchQueryHandler handler;

    Map<String, String[]> queryParams = new HashMap<String, String[]>();

    @Before
    public void doSetup() {
        PowerMockito.mockStatic(MockCloudWatchQueryHandler.class);
        Mockito.when(MockCloudWatchQueryHandler.getInstance()).thenReturn(handler);
        Mockito.when(request.getParameterMap()).thenReturn(queryParams);
    }

    @Test
    public void Test_doPost() throws IOException {
        MockCloudWatchEndpointServlet mockCloudWatchEndpointServlet = new MockCloudWatchEndpointServlet();
        mockCloudWatchEndpointServlet.doPost(request, response);
    }

    @Test
    public void Test_doGet() throws IOException {
        MockCloudWatchEndpointServlet mockCloudWatchEndpointServlet = new MockCloudWatchEndpointServlet();
        mockCloudWatchEndpointServlet.doGet(request, response);
    }

    @Test
    public void Test_doGetWithHeaders() throws IOException {
        HttpServletRequest  mockedRequest = Mockito.mock(HttpServletRequest.class);
        Vector headers = new Vector();
        headers.add("header1");
        Mockito.when(mockedRequest.getHeaderNames()).thenReturn(headers.elements());
        Mockito.when(mockedRequest.getHeader("header1")).thenReturn("header1value");
        MockCloudWatchEndpointServlet mockCloudWatchEndpointServlet = new MockCloudWatchEndpointServlet();
        mockCloudWatchEndpointServlet.doPost(mockedRequest, response);
    }
}
