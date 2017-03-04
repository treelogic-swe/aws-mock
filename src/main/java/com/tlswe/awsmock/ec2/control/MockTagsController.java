package com.tlswe.awsmock.ec2.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tlswe.awsmock.ec2.model.MockTags;

/**
 * Factory class providing static methods for managing life cycle of mock Tags. The current implementations
 * can:
 * <ul>
 * <li>create</li>
 * <li>delete</li>
 * <li>describe</li>
 * </ul>
 * mock Tags. <br>
 *
 *
 * @author Davinder Kumar
 *
 */
public final class MockTagsController {

    /**
     * Singleton instance of MockTagsController.
     */
    private static MockTagsController singletonMockTagsController = null;

    /**
     * A List of all the mock Tags, instanceID as key and {@link MockTags} as value.
     */
    private final List<MockTags> allMockTags = new ArrayList<MockTags>();

    /**
     * Constructor of MockTagsController is made private and only called once by {@link #getInstance()}.
     */
    private MockTagsController() {

    }

    /**
     *
     * @return singleton instance of {@link MockTagsController}
     */
    public static MockTagsController getInstance() {
        if (null == singletonMockTagsController) {
            // "double lock lazy loading" for singleton instance loading on first time usage
            synchronized (MockSubnetController.class) {
                if (null == singletonMockTagsController) {
                    singletonMockTagsController = new MockTagsController();
                }
            }
        }
        return singletonMockTagsController;
    }

    /**
     * List mock Tags instances in current aws-mock.
     *
     * @return a collection all of {@link MockTags} .
     */
    public List<MockTags> describeTags() {
        return allMockTags;
    }

    /**
    * Create the mock Tags.
    * @param resourcesSet List of resourceIds.
    * @param tagSet Map for key, value of tags.
    * @return mock Tags.
    */
    public MockTags createTags(
            final List<String> resourcesSet, final Map<String, String> tagSet) {

        MockTags ret = new MockTags();
        ret.setResourcesSet(resourcesSet);
        ret.setTagSet(tagSet);

        allMockTags.add(ret);
        return ret;
    }

    /**
     * Delete Mock tags.
     *
     * @param resources
     *            resources's tags to be deleted
     * @return Mock tags.
     */
    public boolean deleteTags(final List<String> resources) {

        for (MockTags mockTags : allMockTags) {
           if (mockTags.getResourcesSet().containsAll(resources)) {
               allMockTags.remove(mockTags);
               return true;
           }
        }

        return false;
    }
}