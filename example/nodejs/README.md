Client code examples demonstrating usage of aws-mock with AWS-SDK in JavaScript.

Import note: Endpoint for mock ec2 should be running on "/" (root context) as Amazon's node.js aws-sdk doesn't support endpoint in a directory (while java version of aws-sdk does). And of course, that is because aws-sdk is designed for accessing their own endpoints on root context (https://*.amazonaws.com/) only. 
As in the node.js examples here we use the endpoint at http://localhost:9090/ for calling interfaces on aws-mock. 
Such an endpoint working on root context (like http://localhost:9090/) could be made by proxy-pass actual backend endpoint url http://localhost:8000/aws-mock/ec2-endpoint/ to a front-end reverse proxy web server (such as Apache or Nginx).
