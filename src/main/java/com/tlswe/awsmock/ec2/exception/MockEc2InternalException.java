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
     * default serial version ID for this class which implements
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    public MockEc2InternalException() {
        super();
    }

    public MockEc2InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockEc2InternalException(String message) {
        super(message);
    }

}
