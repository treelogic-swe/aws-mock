aws-mock
========

A lightweight, very modular Java-based mock of essential AWS services, for testing purposes.

## Note ##
For now we implemented only a few interfaces for Amazon ec2: runInstances, stopInstances, startInstances, terminateInstances and describeInstances.   

## To build ##  
Before building from source, you must first generate the Java stubs under com.tlswe.awsmock.ec2.cxf_generated, by running Apache-CXF's wsdl2java: 
    wsdl2java -verbose -d src/main/java -p com.tlswe.awsmock.ec2.cxf_generated -autoNameResolution -server -impl -frontend jaxws21 src/third_party/ec2-2013-02-01.wsdl

And then build the war file: 
    gradle war

## TODO ##
Convert the stuff in cxf_generated into a gradle dependency (as a dependency of sub project). 

