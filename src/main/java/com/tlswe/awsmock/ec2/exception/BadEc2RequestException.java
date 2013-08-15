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
     * default serial version ID for this class which implements
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    public BadEc2RequestException() {
        super();
    }

    public BadEc2RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadEc2RequestException(String message) {
        super(message);
    }

}
