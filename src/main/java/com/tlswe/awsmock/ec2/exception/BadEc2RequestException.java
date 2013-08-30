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
     * Default serial version ID for this class which implements
     * {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
* TODO .
     */
    public BadEc2RequestException() {
        super();
    }

    /**
* TODO .
     *
     * @param message
     *            TODO
     * @param cause
     *            TODO
     */
    public BadEc2RequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
* TODO .
     *
     * @param message
     *            TODO
     */
    public BadEc2RequestException(final String message) {
        super(message);
    }

}
