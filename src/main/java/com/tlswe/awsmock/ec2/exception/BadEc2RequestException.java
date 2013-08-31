package com.tlswe.awsmock.ec2.exception;

import java.io.Serializable;

import com.tlswe.awsmock.common.exception.AwsMockException;

/**
 * Exception for invalid query (parameters) from client.
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
     * Constructs from an error message and the original exception.
     *
     * @param message
     *            the error message
     * @param cause
     *            the original exception
     */
    public BadEc2RequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs from an error message .
     *
     * @param message
     *            the error message
     */
    public BadEc2RequestException(final String message) {
        super(message);
    }

}
