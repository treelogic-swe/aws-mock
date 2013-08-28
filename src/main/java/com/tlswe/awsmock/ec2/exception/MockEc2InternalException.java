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
     * Default serial version ID for this class which implements
     * {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
* TODO .
     */
    public MockEc2InternalException() {
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
    public MockEc2InternalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
* TODO .
     *
     * @param message
     *            TODO
     */
    public MockEc2InternalException(final String message) {
        super(message);
    }

}
