import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/**
 * This example shows how to terminate instances in local aws-mock.
 *
 * @author xma
 *
 */
public final class TerminateInstancesExample {

    /**
     * Private constructor for compliance with checkstyle.
     */
    private TerminateInstancesExample() {

    }


    /**
     * Terminate specified instances (power-on the instances).
     *
     * @param instanceIDs
     *            IDs of the instances to terminate
     * @return a list of state changes for the instances
     */
    public static List<InstanceStateChange> terminateInstances(final List<String> instanceIDs) {
        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        // send the terminate request with args as instance IDs
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(instanceIDs);
        TerminateInstancesResult result = amazonEC2Client.terminateInstances(request);

        return result.getTerminatingInstances();
    }


    /**
     * Main method for command line use.
     *
     * @param args
     *            parameters from command line - IDs of the instances to terminate
     */
    public static void main(final String[] args) {

        List<InstanceStateChange> iscs = terminateInstances(Arrays.asList(args));

        if (null != iscs && iscs.size() > 0) {
            System.out.println("Instance state changes: ");
            for (InstanceStateChange isc : iscs) {
                System.out.println(isc.getInstanceId() + ": " + isc.getPreviousState().getName() + " -> "
                        + isc.getCurrentState().getName());
            }
        } else {
            System.out.println("Nothing happened! Make sure you input the right instance IDs.");
            System.out
                    .println("usage: java TerminateInstancesExample <instanceID-1> [instanceID-2] [instanceID-3] ...");
        }

    }
}
