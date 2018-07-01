package com.tlswe.awsmock.ec2.control;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tlswe.awsmock.ec2.model.MockInternetGateway;
import com.tlswe.awsmock.ec2.model.MockRouteTable;
import com.tlswe.awsmock.ec2.model.MockVpc;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockRouteTableController.class})
public class MockRouteTableControllerTest {

    @Test
    public void Test_getInstance() {
        MockRouteTableController instance = MockRouteTableController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeRouteTables returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeRouteTable() throws Exception {
        
        MockRouteTableController controller = Mockito.spy(MockRouteTableController.class);

        Map<String, MockRouteTable> allMockRouteTable = new ConcurrentHashMap<String, MockRouteTable>();
        MockRouteTable mockRouteTable = new MockRouteTable();
        mockRouteTable.setRouteTableId("rt-324324");
        allMockRouteTable.put("i-2323", mockRouteTable);
        MockRouteTable mockRouteTable1 = new MockRouteTable();
        mockRouteTable1.setRouteTableId("rt-32sdadasd");
        allMockRouteTable.put("i-23223233", mockRouteTable1);

        MemberModifier.field(MockRouteTableController.class, "allMockRouteTables").set(controller,
                allMockRouteTable);

        Collection<MockRouteTable> collectionOfMockRouteTable = controller.describeRouteTables();

        Collection<MockRouteTable> restoreRouteTables = new ArrayList<MockRouteTable>(collectionOfMockRouteTable.size());
        for (MockRouteTable mockRtRes : collectionOfMockRouteTable)
        {
        	restoreRouteTables.add(mockRtRes);
        }
        int collectionCount = collectionOfMockRouteTable.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
        
        for(MockRouteTable restoreVpc : collectionOfMockRouteTable) {
        	controller.deleteRouteTable(restoreVpc.getVpcId());
        }
        
        controller.restoreAllMockRouteTable(restoreRouteTables);
        
        collectionOfMockRouteTable = controller.describeRouteTables();

        collectionCount = collectionOfMockRouteTable.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createRouteTable() throws Exception {
        
       MockRouteTable mockRouteTable = MockRouteTableController
                .getInstance()
                .createRouteTable("TestCDR", "Deafult");

    
        Assert.assertNotNull("Internet gateway created.", mockRouteTable.getRouteTableId());
    }
    
    @Test
    public void Test_deleteRouteTable() throws Exception {
        
       MockRouteTable mockRouteTable = MockRouteTableController
                .getInstance()
                .createRouteTable("TestCDR", "Deafult");
       MockRouteTable mockRouteTableDelete = MockRouteTableController
               .getInstance()
               .deleteRouteTable(mockRouteTable.getRouteTableId());
    
        Assert.assertNotNull("Internet gateway deleted.", mockRouteTableDelete.getRouteTableId());
    }

    @Test
    public void Test_restoreRouteTable() throws Exception {
        
    	 Map<String, MockRouteTable> allMockRouteTable = new ConcurrentHashMap<String, MockRouteTable>();
         MockRouteTable mockRouteTable = new MockRouteTable();
         mockRouteTable.setRouteTableId("rt-324324");
         allMockRouteTable.put("i-2323", mockRouteTable);
         MockRouteTable mockRouteTable1 = new MockRouteTable();
         mockRouteTable1.setRouteTableId("rt-32sdadasd");
         allMockRouteTable.put("i-23223233", mockRouteTable1);
       
         MockRouteTableController.getInstance()
               .restoreAllMockRouteTable(allMockRouteTable.values());
    }

}