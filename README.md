aws-mock [![Build Status](https://travis-ci.org/treelogic-swe/aws-mock.png?branch=master)](https://travis-ci.org/treelogic-swe/aws-mock)
========

A lightweight, very modular mock of essential [AWS services](http://aws.amazon.com/) that works with official aws-sdk, api-tools, or third-party tools, for test automation.  Although the mock is written in Java, it is language-agnostic since its API is exposed via http.

For now we have implemented only a few interfaces (and only necessary data in response) of Amazon EC2: 
- describeImages
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances

This mock of EC2 could be helpful for testing your applications. 'aws-mock' can manage a large amount of mock EC2 instances. 


### How It Works
'aws-mock' works totally as a servlet web application, conforming to the protocols described in the WSDL defined by AWS. 
Basically, take our mock "ec2-endpoint" as an example. It processes [Query Requests](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) built by your client (such as AWS-SDK or EC2-API-Tools) and manages the internal mock EC2 instances as an emulation of the lifecycle of those in genuine EC2 (pending->running, stopping->stopped, terminated, etc), and returns an xml result body in responses which is recognized by your client.  
Note again that, at this time, only the limited EC2 interfaces mentioned have been implemented. And only essential fields of data are filled in the response body. 

Refer to the [Technical Specification](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specification) for more information. 


### Quick Start
```
git clone https://github.com/treelogic-swe/aws-mock.git
cd aws-mock
gradle jettyRun
```
That's all. 

This will run a build that automatically downloads all dependencies and prepares the code for use, and then it will start a jetty server that runs aws-mock locally on your computer. 

Now you are able to interact with your "local Amazon Web Services" (though only EC2 for now), in your own client applications which use [AWS-SDK](http://aws.amazon.com/tools/), or with [EC2-API-TOOLS](http://aws.amazon.com/developertools/Amazon-EC2/351), or with other third-party client tools such as elasticfox. To manage instances on mock EC2, just point to the custom EC2 endpoint like:
`http://localhost:8000/aws-mock/ec2-endpoint/` (equivalent to the official endpoint url like `https://ec2.us-west-1.amazonaws.com/`)

For more usage instructions, please look into our full [User's Guide](https://github.com/treelogic-swe/aws-mock/wiki/User's-Guide).

For detailed specification and reference for those interfaces already available in aws-mock, here is a list of them: [Implemented Requests and Responses](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specification#implemented-requests-and-responses-ec2).


### Javadoc
http://treelogic-swe.github.io/aws-mock/javadoc/


### Tips
- To build a war file for deployment, run `gradle war`. 
- Initially there are no mock instances in mock EC2, so you need to run one or more new instances first. 
- Your client doesn't need to provide valid credentials since aws-mock skips the secretKey/accessKey check. 
- There are a few options in `src/main/resources/aws-mock.properties` to tune. 
- For eclipse users, `gradle cleanEclipse eclipse` will initialize the ready-to-import eclipse wtp project facets. 

### License
'aws-mock' is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

### Your Contribution
Any contribution to aws-mock is strongly welcomed - including any adding of the unimplemented interfaces/data of EC2 and other mock of Amazon Web Services. 
If you find aws-mock helpful in working with your applications and have added features, we encourage you fork and send your pull requests to us! 
Bug reports are also very much appreciated.
