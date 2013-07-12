package com.tlswe.awsmock.ec2.exception;

public class MockEc2Exception extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1826499312425071519L;
    
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
