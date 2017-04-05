package com.tlswe.awsmock.common.exception;

import org.junit.Assert;
import org.junit.Test;

public class AwsMockExceptionTest {

    @Test(expected = AwsMockException.class)
    public void Test_AwsMockExceptionMessageAndThrowable() {
        throw new AwsMockException("AWS Mock Exception", new Exception());
    }

    @Test(expected = AwsMockException.class)
    public void Test_AwsMockExceptionMessageOnly() {
        throw new AwsMockException("AWS Mock Exception");
    }

    @Test(expected = AwsMockException.class)
    public void Test_AwsMockExceptionThrowableOnly() {
        throw new AwsMockException(new Exception());
    }

    @Test
    public void Test_AwsMockExceptionNoArgsConstructor() {
        AwsMockException awsMockException = new AwsMockException();
        Assert.assertNotNull(awsMockException);
    }

}
