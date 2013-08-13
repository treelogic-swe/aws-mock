package com.tlswe.awsmock.ec2.exception;

import java.io.Serializable;

import com.tlswe.awsmock.common.exception.AwsMockException;

/**
 * Generic exception type wrapping for exception raised during life-cycle of
 * mock ec2 management.
 * 
 * @author xma
 * 
 */
public class MockEc2Exception extends AwsMockException {

    /**
     * default serial version ID for this class which implements
     * {@link Serializable}
     * 
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    public MockEc2Exception() {
        super();
        // TODO Auto-generated constructor stub
    }

    public MockEc2Exception(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public MockEc2Exception(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

}
