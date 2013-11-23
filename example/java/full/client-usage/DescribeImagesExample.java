import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Image;

/**
 * This example shows how to describe all available AMIs in local aws-mock.
 *
 * @author xma
 *
 */
public final class DescribeImagesExample {

    /**
     * Private constructor for compliance with checkstyle.
     */
    private DescribeImagesExample() {

    }


    /**
     * Describe all available AMIs within aws-mock.
     *
     * @return a list of AMIs
     */
    public static List<Image> describeAllImages() {
        // pass any credentials as aws-mock does not authenticate them at all
        AWSCredentials credentials = new BasicAWSCredentials("foo", "bar");
        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(credentials);

        // the mock endpoint for ec2 which runs on your computer
        String ec2Endpoint = "http://localhost:8000/aws-mock/ec2-endpoint/";
        amazonEC2Client.setEndpoint(ec2Endpoint);

        // describe all AMIs in aws-mock.
        DescribeImagesResult result = amazonEC2Client.describeImages();

        return result.getImages();
    }


    /**
     * Main method for command line use.
     *
     * @param args
     *            parameters from command line (no need here)
     */
    public static void main(final String[] args) {

        List<Image> images = describeAllImages();

        if (null != images) {
            for (Image image : images) {
                System.out.println(image.getImageId());
            }
        }

    }
}
