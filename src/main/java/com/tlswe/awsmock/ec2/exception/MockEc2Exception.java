package com.tlswe.awsmock.ec2.exception;

/**
 * Generic exception type wrapping for mock ec2 management.
 * 
 * @author xma
 * 
 */
public class MockEc2Exception extends Exception {

    private static final long serialVersionUID = 1L;

    public MockEc2Exception() {
        super();
    }

    public MockEc2Exception(String message) {
        super(message);
    }

    public MockEc2Exception(String message, Throwable cause) {
        super(message, cause);
    }

}
