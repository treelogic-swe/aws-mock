package com.tlswe.awsmock.ec2.exception;

import org.junit.Assert;
import org.junit.Test;

public class BadEc2RequestExceptionTest {

    @Test(expected=BadEc2RequestException.class)
    public void TestBadEc2RequestExceptionActionAndMessage(){
        throw new BadEc2RequestException("Action", "Message");
    }

    @Test(expected=BadEc2RequestException.class)
    public void TestBadEc2RequestExceptionActionMessageAndThrowable(){
        throw new BadEc2RequestException("Action", "Message", new Exception());
    }

    @Test
    public void TestGetAction(){

        BadEc2RequestException exception= new BadEc2RequestException("Action", "Message", new Exception());
        Assert.assertTrue("Action".equals(exception.getAction()));
    }

}
