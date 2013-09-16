package com.tlswe.awsmock.ec2.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;

/**
 * Servlet implementation for mock ec2 endpoint. This servlet works as an AWS ec2 endpoint that accepts AWS Query API
 * request and respond bare xml, with aws-sdk, ec2-api-tools, elasticfox and other clients.
 */
public class MockEc2EndpointServlet extends HttpServlet {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;


    /**
     * Pass the query parameters from client to {@link MockEC2QueryHandler} and write response to client.
     *
     * @param request
     *            request from client of AWS doing the query
     * @param response
     *            response that with an xml body describing the calling result of query from request
     * @throws IOException
     *             throw an I/O exception in case of failing to get the httpServletResponse's writer
     */
    @Override
    protected final void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws
            IOException {

        @SuppressWarnings("unchecked")
        /*-
         * As request.getParameterMap() in servlet-api-2.5 provides return type of raw java.util.Map,
         * we suppress the type safety check warning here.
         */
        Map<String, String[]> queryParams = (Map<String, String[]>) request
                .getParameterMap();

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        MockEC2QueryHandler.getInstance().handle(queryParams, response);

    }


    /**
     * Refer to {@link MockEc2EndpointServlet#doGet}.
     *
     * @param request
     *            see {@link MockEc2EndpointServlet#doGet}
     * @param response
     *            see {@link MockEc2EndpointServlet#doGet}
     * @throws IOException
     *             see {@link MockEc2EndpointServlet#doGet}
     */
    @Override
    protected final void doPost(final HttpServletRequest request,
            final HttpServletResponse response) throws
            IOException {
        doGet(request, response);
    }

}
