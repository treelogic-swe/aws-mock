### Contributing Your Code
You may want to (or even did) add some new features/improvements to aws-mock. Additions highly welcomed, and we will definitely appreciate your contribution to aws-mock!

What follows are some helpful instructions that could make your development process easier and your code fit better for merging to the aws-mock source tree. 

- If you want to add a mock of Amazon web service, let's take Amazon S3 for example, (meaning, you are about to mock S3), you can put S3's WSDL to cxf-stub (the sub project under aws-mock), write a new 'wsdl2java' task according to `cxf-stub/build.gradle` and generate the Java API stub for developing S3 interfaces.
- Add a new servlet as the mock S3 endpoint, which will parse interface call requests from clients.
- Write your logic for mocking behind the servlet.
- Pick and organize the response object by making use of the newly generated S3 cxf-stub.
- Debug the mock S3 endpoint with your favorite AWS SDK.
- Add tests for your code.
- Run `gradle check` in your fork before you send the pull request to make sure your code passes the basic checks and tests.

For sending pull requests to aws-mock, your contribution code can be written in any JVM language - Java, Groovy, Clojure, JRuby, Jython, Scala, etc. 
