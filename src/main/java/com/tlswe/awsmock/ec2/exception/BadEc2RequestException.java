package com.tlswe.awsmock.ec2.exception;

import java.io.Serializable;

import com.tlswe.awsmock.common.exception.AwsMockException;

/**
 * Exception on parsing invalid EC2 Query Request
 * (http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) from client.
 *
 * @author xma
 *
 */
public class BadEc2RequestException extends AwsMockException {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parameter of 'action' name in the Query Request. (Each Query Request should take an 'action' parameter.)
     */
    private final String action;


    /**
     * Constructs a new exception representing a bad EC2 Query Request from client.
     *
     * @param actionOfRequest
     *            the action parameter in query
     * @param message
     *            the error message
     * @param cause
     *            origin error
     */
    public BadEc2RequestException(final String actionOfRequest, final String message, final Throwable cause) {
        super(message, cause);
        this.action = actionOfRequest;
    }


    /**
     * Constructs a new exception representing a bad EC2 Query Request from client.
     *
     * @param actionOfRequest
     *            the action parameter in query
     * @param message
     *            the error message
     */
    public BadEc2RequestException(final String actionOfRequest, final String message) {
        super(message);
        this.action = actionOfRequest;
    }


    /**
     * Get the action name in bad EC2 Query Request that leads to this exception.
     *
     * @return the action name
     */
    public final String getAction() {
        return action;
    }

}
