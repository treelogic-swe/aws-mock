package com.tlswe.awsmock.common.exception;

import org.junit.Test;


public class AwsMockExceptionTest {

    @Test(expected=AwsMockException.class)
    public void TestAwsMockExceptionMessageAndThrowable(){
        throw new AwsMockException("AWS Mock Exception", new Exception());
    }

    @Test(expected=AwsMockException.class)
    public void TestAwsMockExceptionMessageOnly(){
        throw new AwsMockException("AWS Mock Exception");
    }

    @Test(expected=AwsMockException.class)
    public void TestAwsMockExceptionThrowableOnly(){
        throw new AwsMockException(new Exception());
    }

}
