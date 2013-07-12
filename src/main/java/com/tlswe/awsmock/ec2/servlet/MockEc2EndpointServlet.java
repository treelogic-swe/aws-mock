package com.tlswe.awsmock.ec2.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tlswe.awsmock.ec2.control.MockEC2QueryHandler;

/**
 * Servlet implementation class AwsMockServlet
 */
public class MockEc2EndpointServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MockEc2EndpointServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        System.out.println("==== New query comming ====");

        Map<String, String[]> queryParams = request.getParameterMap();
        for (Map.Entry<String, String[]> e : (Set<Map.Entry<String, String[]>>) queryParams
                .entrySet()) {
            System.out.println(e.getKey() + " - (" + e.getValue().length + ") - "
                    + e.getValue()[0]);

        }

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        MockEC2QueryHandler.writeReponse(queryParams, response.getWriter());

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
