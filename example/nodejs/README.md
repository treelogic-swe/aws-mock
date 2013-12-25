## Summary ##
Client code examples demonstrating usage of aws-mock with AWS-SDK in JavaScript.

## Installation ##
1. Install dependencies including aws-sdk by running `npm install`.
2. Add a reverse proxy route for actual backend endpoint url `http://localhost:8000/aws-mock/ec2-endpoint/` to a root directory `http://localhost:9090/`. You can have that done by running `node LocalProxy.js`, or alternatively, add such a proxy route to your favourate web server such as Apache or Nginx. 
3. Run the tests with `node RunExamples.js <options>`.

## Why endpoint on "/" ? ##
Endpoint for mock ec2 should be running on "/" (root context) as Amazon's node.js aws-sdk doesn't support endpoint in a directory (while java version of aws-sdk does). 
And of course, that lack of support is because aws-sdk is designed for accessing their own endpoints on root context (https://*.amazonaws.com/) only. 
As in the node.js examples here we use the endpoint at http://localhost:9090/ for calling interfaces on aws-mock. 
