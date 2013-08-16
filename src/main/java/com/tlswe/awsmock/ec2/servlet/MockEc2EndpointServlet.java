package com.tlswe.awsmock.ec2.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;

/**
 * Servlet implementation for mock ec2 endpoint. This servlet works as an AWS
 * ec2 endpoint that accepts AWS Query API request and respond bare xml, with
 * aws-sdk, ec2-api-tools, elasticfox and other clients.
 */
public class MockEc2EndpointServlet extends HttpServlet {

    /**
     * Log writer for this class.
     */
    private static Logger _log = LoggerFactory.getLogger(MockEc2EndpointServlet.class);

    /**
     * default serial version ID for this class which implements
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    public MockEc2EndpointServlet() {
        super();
    }

    /**
     * * Pass the query parameters from client to {@link MockEC2QueryHandler}
     * and write response to client.
     */
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> queryParams = (Map<String, String[]>) request.getParameterMap();

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        try {
            MockEC2QueryHandler.handle(queryParams, response);
        } catch (AwsMockException e) {
            _log.error("fatal exception caught: {}", e.getMessage());
        }

        // TODO for error response, we need to set http status other than 200

    }

    /**
     * @see #doGet
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        doGet(request, response);
    }

}
