package simple;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
 *
 * @author xma
 *
 */
public class DescribeInstancesExample {

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

        // DescribeInstancesRequest request = new DescribeInstancesRequest();
        // describe all instances
        DescribeInstancesResult response = amazonEC2Client.describeInstances();
        List<Reservation> reservations = response.getReservations();

        System.out.println("All instances: ");
        for (Reservation reservation : reservations) {
            List<Instance> instances = reservation.getInstances();

            if (null != instances) {

                for (Instance i : instances) {
                    System.out.println(i.getInstanceId() + " - " + i.getState().getName());
                }
            }
        }

    }

}
