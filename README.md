aws-mock
========

A lightweight, very modular Java-based mock of essential AWS services, works with official aws-sdk, or third-party tools such as elasticfox, generally for testing purposes.

For now we implemented only a few interfaces (and only necessary data in response) of Amazon EC2: 
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances
- describeImages

This mock of EC2 could be helpful for testing your applications with which you need to simulate large amount of dummy EC2 instances. 


### Quick Start
```
git clone https://github.com/treelogic-swe/aws-mock.git
cd aws-mock
gradle run
```
That's all!
Necessary stuff will be automatically built and a jetty server will be started running aws-mock locally on your computer. 
Now you can interact with your "local Amazon Web Services" (though only EC2 implemented for now). 
In your own client applications which use [aws-sdk](http://aws.amazon.com/tools/), or with [EC2-API-Tools](http://aws.amazon.com/developertools/Amazon-EC2/351), or with any other third-party client tools, to manage instances on mock EC2, just point to the custom EC2 endpoint like:
`http://localhost:8000/aws-mock/ec2-endpoint/` (which is equivalent to the official one, e.g. `https://ec2.us-west-1.amazonaws.com/`)


### How It Works
Aws-mock works totally as a servlet web application, conform to the protocols defined in the WSDL defined by AWS. 
Basically, take our mock "ec2-endpoint" as an example, it processes [Query Requests](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) built by your client (such as AWS-SDK or EC2-API-Tools) and manages the internal mock EC2 instances as emulation of the lifecycle (pending->running->stopping->stopped->terminated, etc) of those in genuine EC2, and returns xml result response which is recognized by your client.  
Note again that nly limited EC2 interfaces mentioned have been implemented. And only essential fields of data are filled in the response to calling of an interface. 


### Tips
- To build war for deployment, run `gradle war`. 
- Initially there is no mock instances in mock EC2, so you need to run a few new instances first. 
- Your client doesn't need to provide valid credentials since aws-mock skips the secretKey/accessKey check. 
- There are a few options in `src/main/resources/aws-mock.properties` to tune. 
- For eclipse users, `gradle cleanEclipse eclipse` can initialize the ready-to-import eclipse wtp project facets. 


### Your Contribution
Any contribution to aws-mock is strongly welcomed - including any adding of the unimplemented interfaces/data of EC2 and other mock of Amazon Web Services. 
If you find aws-mock helpful working with your applications and have features added, we encourage you fork and send your pull requests to us! 
Bug report is also well appreciated. Thanks for your participation in advance. 
