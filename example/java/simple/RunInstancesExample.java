package simple;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

/**
 *
 * @author xma
 *
 */
public class RunInstancesExample {

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {

        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        /*-
         * use one of the pre-defined amis
         * (all amis are pre-defined in aws-mock-default.properties or aws-mock.properties)
         */
        String imageId = "ami-12345678";

        // instance type
        String instanceType = "m1.large";

        // run 10 instances
        RunInstancesRequest request = new RunInstancesRequest();
        request.withImageId(imageId).withInstanceType(instanceType)
                .withMinCount(10).withMaxCount(10);
        RunInstancesResult result = amazonEC2Client.runInstances(request);

        List<Instance> instances = result.getReservation().getInstances();

        System.out.println("Started instances: ");

        if (null != instances) {
            for (Instance i : instances) {
                System.out.println(i.getInstanceId() + " - " + i.getState().getName());
            }
        }

    }

}
