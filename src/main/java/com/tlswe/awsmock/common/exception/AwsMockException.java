package com.tlswe.awsmock.common.exception;

/**
 * Generic exception in aws-mock.
 *
 * @author xma
 *
 */
public class AwsMockException extends RuntimeException {

    /**
     * Default serial version ID for this class which implements {@link java.io.Serializable}.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;


    /**
    *
    */
    public AwsMockException() {
        super();
    }


    /**
     *
     * @param message
     *            message describing the exception
     */
    public AwsMockException(final String message) {
        super(message);
    }


    /**
     *
     * @param message
     *            message describing the exception
     * @param cause
     *            cause exception
     */
    public AwsMockException(final String message, final Throwable cause) {
        super(message, cause);
    }


    /**
     *
     * @param cause
     *            cause exception
     */
    public AwsMockException(final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
