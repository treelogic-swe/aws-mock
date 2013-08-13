aws-mock
========

A lightweight, very modular Java-based mock of essential AWS services, works with official aws-sdk, or third-party tools such as elasticfox, generally for testing purposes.

For now we implemented only a few interfaces of Amazon EC2: 
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances
- describeImages

This mock of EC2 could be helpful for testing your applications with which you need to simulate large amount of dummy EC2 instances. 


### To Build
Before first time of building from source, you must generate the Java stubs under `com.tlswe.awsmock.ec2.cxf_generated`, by running [Apache-CXF](http://cxf.apache.org/)'s wsdl2java tool: 
```
wsdl2java -verbose -d src/main/java -p com.tlswe.awsmock.ec2.cxf_generated \
-autoNameResolution -impl -server -frontend jaxws21 src/third_party/ec2-2013-02-01.wsdl
```

And then build the war file: 
```
gradle war
```

### Usage

Deploy the war to your servlet container, and in your code with aws-sdk, or EC2 client tools, just point to the custom ec2 endpoint such as:
```
http://localhost:8080/aws-mock/ec2-endpoint/
```

### Tips
For eclipse users, `gradle cleanEclipse eclipse` can initialize the ready-to-import eclipse project artifacts. 


### To-do List
- Organize the web service Java stub jar generating with CXF into a gradle dependency for other parts of builds (probably as a dependent gradle sub project). 
- Make sure to be compatible with Amazon EC2 API Tools. 
- Persistence of mock objects for recovering after service restarts. 
- Clean up terminated mock instances after a pre-defined period (as genuine EC2 does). 
- [Done] <del>Write an error xml response for all those unimplemented actions (in MockEC2QueryHandler.writeReponse). </del>
- [Done] <del>Improve the exception handling. </del>
