aws-mock automated testing [![Build Status](https://travis-ci.org/treelogic-swe/aws-mock.png?branch=master)](https://travis-ci.org/treelogic-swe/aws-mock)
========

### Overview

Easily test essential AWS services with the lightweight, very modular aws-mock. Works with official aws-sdk, api-tools and third-party tools. 
Completely and safely automates the testing process of AWS services in a mock EC2 environment, using http to expose the API.

Readily accessible: Ready-to-run examples included for NodeJS and Java. aws-mock is available as a package [from npmjs.org](https://npmjs.org/package/aws-mock) and [from maven.org](http://search.maven.org/#browse%7C-1342745620).

Aws-mock currently features the following implemented interfaces of Amazon EC2: 
- describeImages
- runInstances
- stopInstances
- startInstances
- terminateInstances
- describeInstances

This mock can manage a huge amount of EC2 instances, making it super easy for you to test your applications. 

aws-mock is a pure servlet web application, conforming to the protocols described in the WSDL defined by AWS.  Contributions in any JVM-targeting language are welcome. 


### How It Works
Taking for example the ec2 interface that aws-mock supports (the ec2-endpoint mock), this mock processes [Query Requests](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-query-api.html) built by your client.

It then manages the internal mock EC2 instances as an emulation of the lifecycle of those in genuine EC2 (pending->running, stopping->stopped, terminated, etc).

In response it then returns an xml result body which is recognized by your client.

Note: At this time, the only interfaces that have been implemented are the ones listed above. Only essential data fields in the response body are filled.

Inside aws-mock, the mock EC2 instances can work in their own program threads and behave just like real ones.  Thus, by extending aws-mock, it would be easy for you to add any custom behavior that you desire to your mock EC2 instances (e.g. insert 'agents' that send heartbeat pings to your EC2 cluster controller, etc).  This will enable you to mock and test your EC2-based system more precisely. 
 
For more information, please refer to the [Technical Specifications](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specifications). 


### Quick Start
```
git clone https://github.com/treelogic-swe/aws-mock.git
cd aws-mock
gradle jettyRun
```
That's all. 

This will run a build that automatically downloads all dependencies and prepares the code for use, and then it will start a jetty server that runs aws-mock locally on your computer.
 
Now you are able to interact with your local, mock version of Amazon Web Services (though only EC2 for now), in your own client applications.

You can use [AWS-SDK](http://aws.amazon.com/tools/), or a number of other third-party client tools such as elasticfox. 

To manage instances on mock EC2, just point to the custom EC2 endpoint as follows: 
http://localhost:8000/aws-mock/ec2-endpoint/ (equivalent to the official endpoint url https://ec2.us-west-1.amazonaws.com/)

For more usage instructions including how to extend aws-mock, please look into our full [User's Guide](https://github.com/treelogic-swe/aws-mock/wiki/User's-Guide).

For detailed specification and reference for those interfaces already available in aws-mock, here is a list of them: [Implemented Requests and Responses](https://github.com/treelogic-swe/aws-mock/wiki/Technical-Specifications#implemented-requests-and-responses-ec2).


### API Documentation
Please find API documentation for all currently implemented interfaces at the link below. Don't worry about the word 'javadoc' – you don't have to write any Java, just pick your favorite JVM-targeting language (Clojure, Scala, JRuby, Jython, Groovy, etc.) and off you go. The aws-mock contributors commit to supporting [Literate Programming](http://en.wikipedia.org/wiki/Literate_programming) in any JVM-targeting language you choose to use.

http://treelogic-swe.github.io/aws-mock/javadoc/


### Tips
- To build a war file for deployment, run `gradle war`.
- Initially there are no mock instances in mock EC2, so you need to run one or more new instances first. 
- Your client doesn't need to provide valid credentials since aws-mock skips the `secretKey`/`accessKey` check. 
- There are a few options in `src/main/resources/aws-mock.properties` to tune.
- For Eclipse users, `gradle clean Eclipse eclipse` will initialize the ready-to-import eclipse wtp project facets. 


### Your Contribution, in Any JVM-Targeting Language
Any contribution to aws-mock is strongly welcomed - including any adding of the unimplemented interfaces/data of EC2 and other mock of Amazon Web Services. If you find aws-mock helpful in working with your applications and have added features, we encourage you fork and send your pull requests to us! Bug reports are also very much appreciated.

Adding features to aws-mock is really easy, and you can do it in any JVM language you want. Please see the [instructions](http://treelogic-swe.github.io/aws-mock/mdwiki.html#!contributing.md).

### License
aws-mock is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
