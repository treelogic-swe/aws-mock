package com.tlswe.awsmock.ec2.exception;

import java.io.Serializable;

import com.tlswe.awsmock.common.exception.AwsMockException;

/**
 * Exception during life-cycle processing of Mock EC2 on server side.
 *
 * @author xma
 *
 */
public class MockEc2InternalException extends AwsMockException {

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
    public MockEc2InternalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs from an error message .
     *
     * @param message
     *            the error message
     */
    public MockEc2InternalException(final String message) {
        super(message);
    }

}
