aws-mock [![Build Status](https://travis-ci.org/treelogic-swe/aws-mock.png?branch=master)](https://travis-ci.org/treelogic-swe/aws-mock)
========

### Overview

Easily test essential AWS services with the lightweight, very modular aws-mock. Works with official aws-sdk, api-tools and third-party tools. 
Completely and safely automates the testing process of AWS services in a mock EC2 environment, using http to expose the API.

Aws-mock currently features the following implemented interfaces of Amazon EC2: 
- describeImages
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances

This mock can manage a huge amount of EC2 instances, making it super easy for you to test your applications. 

Aws-mock is a pure servlet web application, conforming to the protocols described in the WSDL defined by AWS. 


### How It Works
Example: our ec2-endpoint mock. 
This mock processes [Query Requests](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) built by your client (such as aws-sdk).

It then manages the internal mock EC2 instances as an emulation of the lifecycle of those in genuine EC2 (pending->running, stopping->stopped, terminated, etc).

In response it then returns an xml result body which is recognized by your client.

Note: at this time, the only interfaces that have been implemented are the ones listed above. Only essential data fields in the response body are filled.
 
For more information, please the [Technical Specifications](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specifications). 


### Quick Start
```
git clone https://github.com/treelogic-swe/aws-mock.git
cd aws-mock
gradle jettyRun
```
That's all. 

This will run a build that automatically downloads all dependencies and prepares the code for use, and then it will start a jetty server that runs aws-mock locally on your computer.
 
Now you are able to interact with your local, mock version of Amazon Web Services (though only EC2 for now), in your own client applications.

You can use [AWS-SDK](http://aws.amazon.com/tools/), or with [EC2-API-TOOLS](http://aws.amazon.com/developertools/Amazon-EC2/351) or a number of other third-party client tools such as elasticfox. 

To manage instances on mock EC2, just point to the custom EC2 endpoint as follows: 
http://localhost:8000/aws-mock/ec2-endpoint/ (equivalent to the official endpoint url https://ec2.us-west-1.amazonaws.com/)

For more usage instructions, please look into our full [User's Guide](https://github.com/treelogic-swe/aws-mock/wiki/User's-Guide).

For detailed specification and reference for those interfaces already available in aws-mock, here is a list of them: [Implemented Requests and Responses](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specifications#implemented-requests-and-responses-ec2).


### Packages
All packages for currently implemented interfaces can be found here. Don't worry about the word 'javadoc' – you don't have to write any Java, just pick your favorite language (Clojure, Jython etc) and off you go. 

http://treelogic-swe.github.io/aws-mock/javadoc/


### Tips
- To build a war file for deployment, run gradle war.
- Initially there are no mock instances in mock EC2, so you need to run one or more new instances first. 
- Your client doesn't need to provide valid credentials since aws-mock skips the secretKey/accessKey check. 
- There are a few options in src/main/resources/aws-mock.properties to tune.
- For eclipse users, gradle clean Eclipse eclipse will initialize the ready-to-import eclipse wtp project facets. 


### License
'aws-mock' is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).


### Your Contribution
Any contribution to aws-mock is strongly welcomed - including any adding of the unimplemented interfaces/data of EC2 and other mock of Amazon Web Services. If you find aws-mock helpful in working with your applications and have added features, we encourage you fork and send your pull requests to us! Bug reports are also very much appreciated.
