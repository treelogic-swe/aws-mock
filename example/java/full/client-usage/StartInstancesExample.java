import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;

/**
 * This example shows how to start existing powered-off instances in local aws-mock.
 *
 * @author xma
 *
 */
public final class StartInstancesExample {

    /**
     * Private constructor for compliance with checkstyle.
     */
    private StartInstancesExample() {

    }


    /**
     * Start specified instances (power-on the instances).
     *
     * @param instanceIDs
     *            IDs of the instances start
     * @return a list of state changes for the instances
     */
    public static List<InstanceStateChange> startInstances(final List<String> instanceIDs) {
        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        // send the start request with args as instance IDs to start existing instances
        StartInstancesRequest request = new StartInstancesRequest();
        request.withInstanceIds(instanceIDs);
        StartInstancesResult result = amazonEC2Client.startInstances(request);

        return result.getStartingInstances();
    }


    /**
     * Main method for command line use.
     *
     * @param args
     *            parameters from command line - IDs of the instances to start
     */
    public static void main(final String[] args) {

        List<InstanceStateChange> iscs = startInstances(Arrays.asList(args));

        if (null != iscs && iscs.size() > 0) {
            System.out.println("Instance state changes: ");
            for (InstanceStateChange isc : iscs) {
                System.out.println(isc.getInstanceId() + ": " + isc.getPreviousState().getName() + " -> "
                        + isc.getCurrentState().getName());
            }
        } else {
            System.out.println("Nothing happened! Make sure you input the right instance IDs.");
            System.out.println("usage: java StartInstancesExample <instanceID-1> [instanceID-2] [instanceID-3] ...");
        }

    }
}
