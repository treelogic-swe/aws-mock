package simple;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class RunInstancesExample {

    /**
     * Log writer for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(RunInstancesExample.class);


    /**
     * @param args
     */
    public static void main(final String[] args) {

        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        String ec2Endpoint = "http://localhost:8080/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        String imageId = "ami-12345678";
        String instanceType = "m1.large";

        RunInstancesRequest request = new RunInstancesRequest();
        request.withImageId(imageId).withInstanceType(instanceType)
                .withMinCount(10).withMaxCount(10);

        RunInstancesResult result = amazonEC2Client.runInstances(request);
        List<Instance> instances = result.getReservation().getInstances();

        log.info("started instances: ");

        if (null != instances) {
            for (Instance i : instances) {
                log.info(i.getInstanceId());
            }
        }

    }

}
