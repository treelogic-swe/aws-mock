### Contribute Your Code to aws-mock – All JVM Languages Welcome!
You may want to add some new features/improvements to aws-mock (or maybe you already did). Additions are extremely welcome and we would immensely appreciate your contribution to aws-mock!

What follows are some helpful instructions that will make your development process easier.
It will also make your contribution a better fit for the aws-mock source tree.

For example, let's say you want to add a mock of Amazon web service such as S3:

- To mock S3, you put S3's WSDL to `cxf-stub` (the sub project under aws-mock)
- Then write a new 'wsdl2java' task according to `cxf-stub/build.gradle`
- If you are using Java, generate the Java API stub for developing S3 interfaces.  Otherwise, build the equivalent to suit.
- Add a new servlet as the mock S3 endpoint, which will parse interface call requests from clients
- Write your logic for mocking behind the servlet
- Pick and organize the response object by making use of the newly generated S3 cxf-stub or the equivalent.
- Debug the mock S3 endpoint with your favorite AWS SDK
- Add tests for your code
- Run `gradle check` in your fork before you send the pull request to make sure your code passes the basic checks and tests

And that's all there is to it.

Except for the final, juicy bit: You can CONTRIBUTE YOUR CODE IN ANY JVM LANGUAGE YOU LIKE – Clojure, Scala, Groovy, JRuby, Jython, and of course Java if that's your thing.

We're looking forward to your contributions – surprise us!
