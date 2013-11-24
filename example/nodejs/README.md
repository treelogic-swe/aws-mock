## Summary ##
Client code examples demonstrating usage of aws-mock with AWS-SDK in JavaScript.

## Installation ##
1. Install aws-sdk by running `npm install -g aws-sdk`.
2. In your web server such as Nginx or Apache, add a reverse proxy route for actual backend endpoint url `http://localhost:8000/aws-mock/ec2-endpoint/` to a root directory `http://localhost:9090/`.
3. Run the tests with node.

## Why endpoint on "/" ? ##
Endpoint for mock ec2 should be running on "/" (root context) as Amazon's node.js aws-sdk doesn't support endpoint in a directory (while java version of aws-sdk does). 
And of course, that is because aws-sdk is designed for accessing their own endpoints on root context (https://*.amazonaws.com/) only. 
As in the node.js examples here we use the endpoint at http://localhost:9090/ for calling interfaces on aws-mock. 
