/**
 * File name: IntegrationTest.java Author: Willard Wang Create date: Aug 8, 2013
 */
package test.com.tlswe.awsmock;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;

/**
 * @author Willard Wang
 * 
 */
public class BaseTest {
    protected static AWSCredentials credentials;
    protected static AmazonEC2Client amazonEC2Client;

    @BeforeClass
    public static void setup() {
        credentials = new BasicAWSCredentials("replaceme", "replaceme");
        amazonEC2Client = new AmazonEC2Client(credentials);
        amazonEC2Client
                .setEndpoint("http://localhost:8000/aws-mock/ec2-endpoint");
    }

    @Test
    public void sampleTest() throws IOException {
        // TODO add test code
    }
}
