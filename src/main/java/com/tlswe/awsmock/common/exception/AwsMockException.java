package com.tlswe.awsmock.common.exception;

/**
 * Generic exception in aws-mock.
 * 
 * @author xma
 * 
 */
public class AwsMockException extends Exception {

    /**
     * Default serial version ID for this class which implements.
     * {@link java.io.Serializable}
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
     */
    public AwsMockException(String message) {
        super(message);
    }

    /**
     * 
     */
    public AwsMockException(String message, Throwable cause) {
        super(message, cause);
    }

}