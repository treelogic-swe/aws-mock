package com.tlswe.awsmock.common.exception;

import java.io.Serializable;

/**
 * Generic exception in aws-mock.
 * 
 * @author xma
 * 
 */
public class AwsMockException extends Exception {

    /**
     * default serial version ID for this class which implements
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    public AwsMockException() {
        super();
    }

    public AwsMockException(String message) {
        super(message);
    }

    public AwsMockException(String message, Throwable cause) {
        super(message, cause);
    }

}