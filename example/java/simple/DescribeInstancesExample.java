import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
 * This example shows how to describe all instances in local aws-mock.
 *
 * @author xma
 *
 */
public final class DescribeInstancesExample {

    /**
     * Private constructor for compliance with checkstyle.
     */
    private DescribeInstancesExample() {

    }


    /**
     * Describe all mock instances within aws-mock.
     *
     * @return a list of all instances
     */
    public static List<Instance> describeAllInstances() {
        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        DescribeInstancesResult response = amazonEC2Client.describeInstances();
        List<Reservation> reservations = response.getReservations();

        List<Instance> ret = new ArrayList<Instance>();

        for (Reservation reservation : reservations) {
            List<Instance> instances = reservation.getInstances();

            if (null != instances) {

                for (Instance i : instances) {
                    ret.add(i);
                }
            }
        }

        return ret;
    }


    /**
     * Describe specified instances within aws-mock.
     *
     * @param instanceIDs
     *            a list of instance IDs to describe
     * @return a list of specified instances
     */
    public static List<Instance> describeInstances(final List<String> instanceIDs) {
        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withInstanceIds(instanceIDs);

        DescribeInstancesResult response = amazonEC2Client.describeInstances(request);
        List<Reservation> reservations = response.getReservations();

        List<Instance> ret = new ArrayList<Instance>();

        for (Reservation reservation : reservations) {
            List<Instance> instances = reservation.getInstances();

            if (null != instances) {

                for (Instance i : instances) {
                    ret.add(i);
                }
            }
        }

        return ret;
    }


    /**
     * Main method for command line use.
     *
     * @param args
     *            parameters from command line (no need here)
     */
    public static void main(final String[] args) {

        // describe all instances in aws-mock
        List<Instance> allInstances = describeAllInstances();
        for (Instance i : allInstances) {
            System.out.println(i.getInstanceId() + " - " + i.getState().getName());
        }

        // describe specifiled instances in aws-mock
        List<Instance> someInstances = describeInstances(Arrays.asList("i-12345678", "i=abcdef00"));
        for (Instance i : someInstances) {
            System.out.println(i.getInstanceId() + " - " + i.getState().getName());
        }
    }

}
