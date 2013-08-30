aws-mock [![Build Status](https://travis-ci.org/treelogic-swe/aws-mock.png?branch=master)](https://travis-ci.org/treelogic-swe/aws-mock)
========

A lightweight, very modular Java-based mock of essential [AWS services](http://aws.amazon.com/), works with official aws-sdk, api-tools, or third-party tools, generally for testing purposes.

For now we have implemented only a few interfaces (and only necessary data in response) of Amazon EC2: 
- describeImages
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances

This mock of EC2 could be helpful for testing your applications with which for testing you need to simulate and manage large amount of mock EC2 instances. 


### How It Works
Aws-mock works totally as a servlet web application, conforming to the protocols described in the WSDL defined by AWS. 
Basically, take our mock "ec2-endpoint" as an example, it processes [Query Requests](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) built by your client (such as AWS-SDK or EC2-API-Tools) and manages the internal mock EC2 instances as emulation of the lifecycle of those in genuine EC2(pending->running, stopping->stopped, terminated, etc), and returns xml result body in responses which is recognized by your client.  
Note again that only limited EC2 interfaces mentioned have been implemented. And only essential fields of data are filled in the response body. 


### Quick Start
```
git clone https://github.com/treelogic-swe/aws-mock.git
cd aws-mock
gradle jettyRun
```
That's all. 

Necessary stuff will be all automatically built with dependencies downloaded and a jetty server will be started running aws-mock locally on your computer. 

Now you are able to interact with your "local Amazon Web Services" (though only EC2 for now). 
In your own client applications which use [AWS-SDK](http://aws.amazon.com/tools/), or with [EC2-API-TOOLS](http://aws.amazon.com/developertools/Amazon-EC2/351), or with other third-party client tools such as elasticfox. To manage instances on mock EC2, just point to the custom EC2 endpoint like:
`http://localhost:8000/aws-mock/ec2-endpoint/` (equivalent to the official endpoint url like `https://ec2.us-west-1.amazonaws.com/`)

For detailed specification and usage reference for those interfaces already available in aws-mock, here is a list of [Implemented Requests and Responses](https://github.com/treelogic-swe/aws-mock/wiki/Implemented-Requests-and-Responses).


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
